package dev.waldq.rostelekom.data.remote

import dev.waldq.rostelekom.data.repository.AuthApi
import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.models.RefreshTokenDto
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Единственное место, где создаётся Retrofit. API-интерфейсы получают отсюда через [create]:
 *
 * `val authApi = ApiClient.create<AuthApi>()`
 *
 * Создавать их нужно в `AppContainer`, а репозитории принимают их в конструктор.
 */
object ApiClient {
    // TODO адрес бэка. 10.0.2.2 - это localhost компьютера, если запускаешь на эмуляторе.
    //  Для http (не https) в манифесте стоит android:usesCleartextTraffic="true"
    private const val BASE_URL = "http://192.168.1.142:8080/"

    // DTO в проекте на kotlinx.serialization, поэтому парсим через Json, а не через Gson
    internal val json = Json {
        ignoreUnknownKeys = true // бэк может прислать поля, которых нет в наших DTO
    }

    @Volatile
    var accessTokenProvider: () -> String? = { null }
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS).
        addInterceptor { chain ->
            val request = chain.request()
            val token = accessTokenProvider()
            if (token == null || request.url.encodedPath.startsWith("/api/auth")) chain.proceed(request)
            else chain.proceed(request.newBuilder().header("Authorization", "Bearer $token").build())
        }
        // TODO оставить только в debug: на уровне BODY в логи попадают телефоны и токены
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .authenticator { _, response ->
            val request = response.request
            if (request.url.encodedPath.startsWith("/api/auth") || response.priorResponse != null) {
                return@authenticator null
            }

            synchronized(this) {
                val storage = AppContainer.tokenStorage
                val sentToken = request.header("Authorization")?.removePrefix("Bearer ")
                val savedToken = runBlocking { storage.getAccessToken() }
                if (savedToken != null && savedToken != sentToken) {
                    return@authenticator request.newBuilder()
                        .header("Authorization", "Bearer $savedToken").build()
                }

                val refreshToken = runBlocking { storage.getRefreshToken() } ?: return@authenticator null
                val tokens = try {
                    runBlocking { create<AuthApi>().refresh(RefreshTokenDto(refreshToken)) }
                } catch (e: HttpException) {
                    if (e.code() in 400..499) runBlocking { storage.clear() }
                    return@authenticator null
                } catch (e: IOException) {
                    return@authenticator null
                }

                val access = tokens.accessToken ?: return@authenticator null
                val refresh = tokens.refreshToken ?: return@authenticator null
                runBlocking { storage.save(access, refresh) }
                request.newBuilder().header("Authorization", "Bearer $access").build()
            }
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    inline fun <reified T> create(): T = create(T::class.java)


}