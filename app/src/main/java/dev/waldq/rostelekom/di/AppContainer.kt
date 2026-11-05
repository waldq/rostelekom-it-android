package dev.waldq.rostelekom.di

import android.annotation.SuppressLint
import android.content.Context
import dev.waldq.rostelekom.data.local.TokenStorage
import dev.waldq.rostelekom.data.remote.ApiClient
import dev.waldq.rostelekom.data.repository.AuthApi
import dev.waldq.rostelekom.data.repository.RemoteAuthRepository
import dev.waldq.rostelekom.data.repository.RemoteUserRepository
import dev.waldq.rostelekom.data.repository.UserApi

// Здесь создаются и хранятся все репозитории.
object AppContainer {
    @SuppressLint("StaticFieldLeak")
    lateinit var tokenStorage: TokenStorage // потенациально может быть утечка памяти
        private set

    fun init(context: Context) {
        tokenStorage = TokenStorage(context.applicationContext)
    }
    val authRepository: AuthApi by lazy {
        RemoteAuthRepository(api = ApiClient.create<AuthApi>(), tokenStorage = tokenStorage)
    }

    val userRepository: UserApi by lazy {
        RemoteUserRepository(api = ApiClient.create<UserApi>())
    }
}