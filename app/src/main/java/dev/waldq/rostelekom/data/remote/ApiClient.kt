package dev.waldq.rostelekom.data.remote

import android.R.attr.level
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.getValue

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
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // DTO в проекте на kotlinx.serialization, поэтому парсим через Json, а не через Gson
    private val json = Json {
        ignoreUnknownKeys = true // бэк может прислать поля, которых нет в наших DTO
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        // TODO оставить только в debug: на уровне BODY в логи попадают телефоны и токены
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        // TODO когда появятся токены: addInterceptor, который добавляет заголовок Authorization
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