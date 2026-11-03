package dev.waldq.rostelekom.di

import dev.waldq.rostelekom.data.repository.AuthRepository
import dev.waldq.rostelekom.data.repository.FakeAuthRepository

// Здесь создаются и хранятся все репозитории.
object AppContainer {
    // позже поменяется на нормальный репозиторий, взаимодействующий с бэком
    val authRepository: AuthRepository by lazy { FakeAuthRepository() } // TODO
}