package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.models.OtpChallengeDto
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.models.OtpVerifyDto
import dev.waldq.rostelekom.models.TokenPairDto

/**
 * Интерфейс репозитория, работающий с запросами на авторизацию.
 *
 * С ним взаимодействует ViewModel экрана логина,
 * чтобы UI не видел непосредственного кода репозитория.
 */
interface AuthRepository {
    /**
     * Функция для запроса на получение OTP кода.
     *
     * Отправляет запрос на `/api/auth/otp/request` в виде [OtpRequestDto].
     *
     * @param phone номер телефона в формате +79991234567.
     */
    suspend fun requestCode(phone: String): OtpChallengeDto

    /**
     * Функция для отправки кода и получения пары токенов.
     *
     * Отправляет запрос на `/api/auth/otp/verify` в виде [OtpVerifyDto]
     *
     * @param phone номер телефона в формате +79991234567.
     * @param code OTP код. Пока что 6 цифр.
     */
    suspend fun verifyCode(phone: String, code: String): TokenPairDto
}