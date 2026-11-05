package dev.waldq.rostelekom.di

import dev.waldq.rostelekom.data.remote.ApiClient
import dev.waldq.rostelekom.data.remote.AuthApi
import dev.waldq.rostelekom.data.repository.RemoteAuthRepository

// Здесь создаются и хранятся все репозитории.
object AppContainer {
    // позже поменяется на нормальный репозиторий, взаимодействующий с бэком
    //val authRepository: AuthRepository by lazy { FakeAuthRepository() } // TODO
    val authRepository: AuthApi by lazy {
        RemoteAuthRepository(ApiClient.create<AuthApi>())
    }
}