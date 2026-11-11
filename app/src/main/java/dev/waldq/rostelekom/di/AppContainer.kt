package dev.waldq.rostelekom.di

import android.annotation.SuppressLint
import android.content.Context
import dev.waldq.rostelekom.data.local.TokenStorage
import dev.waldq.rostelekom.data.remote.ApiClient
import dev.waldq.rostelekom.data.repository.AuthApi
import dev.waldq.rostelekom.data.repository.BillingApi
import dev.waldq.rostelekom.data.repository.RemoteAuthRepository
import dev.waldq.rostelekom.data.repository.RemoteBillingRepository
import dev.waldq.rostelekom.data.repository.RemoteUserRepository
import dev.waldq.rostelekom.data.repository.UserApi
import kotlinx.coroutines.runBlocking

// Здесь создаются и хранятся все репозитории.
object AppContainer {
    @SuppressLint("StaticFieldLeak")
    lateinit var tokenStorage: TokenStorage // потенациально может быть утечка памяти
        private set

    fun init(context: Context) {
        tokenStorage = TokenStorage(context.applicationContext)
        // OkHttp вызывает interceptor не на главном потоке, поэтому блокировка здесь безопасна
        ApiClient.accessTokenProvider = { runBlocking { tokenStorage.getAccessToken() } }
    }
    val authRepository: AuthApi by lazy {
        RemoteAuthRepository(api = ApiClient.create<AuthApi>(), tokenStorage = tokenStorage)
    }

    val userRepository: UserApi by lazy {
        RemoteUserRepository(api = ApiClient.create<UserApi>())
    }

    val billingRepository: BillingApi by lazy {
        RemoteBillingRepository(api = ApiClient.create<BillingApi>())
    }
}