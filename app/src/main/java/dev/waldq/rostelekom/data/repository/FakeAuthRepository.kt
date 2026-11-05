package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.models.OtpChallengeDto
import dev.waldq.rostelekom.models.TokenPairDto
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeAuthRepository : AuthRepository {
    // мапа { номер телефона -> код } для хранения последнего кода для номера
    private val codes = mutableMapOf<String, String>()

    /**
     * Функция для запроса на получение OTP кода.
     *
     * Отправляет запрос на `/api/auth/otp/request` в виде [OtpRequestDto]. Конкретно здесь ничего не отправляется.
     *
     * @param phone номер телефона в формате +79991234567.
     */
    override suspend fun requestCode(phone: String): OtpChallengeDto {
        // имитация задержки, чтобы на кнопке успевал поработать индикатор загрузки
        delay(800.milliseconds)

        // имитация ошибок с бэка, чтобы не рандомизировать
        when {
            phone.endsWith("0000") -> throw ApiException(
                statusCode = 429,
                message = "Код уже отправлен, повторите через 30 с",
                retryAfterSeconds = 30,
                )
            phone.endsWith("1111") -> throw ApiException(
                statusCode = 503,
                message = "Сервис временно недоступен. Попробуйте позже.",
                )
        }

        val code = (100000..999999).random().toString()
        codes[phone] = code

        return OtpChallengeDto(
            phone = phone,
            code = code,
            expiresInSeconds = 300,
            resendAfterSeconds = 60
        )
    }

    /**
     * Функция для отправки кода и получения пары токенов.
     *
     * Отправляет запрос на `/api/auth/otp/verify` в виде [OtpVerifyDto]. Конкретно здесь ничего не отправляется.
     *
     * @param phone номер телефона в формате +79991234567.
     * @param code OTP код. Пока что 6 цифр.
     */
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun verifyCode(
        phone: String,
        code: String,
    ): TokenPairDto {
        // имитация задержки, чтобы на кнопке успевал поработать индикатор загрузки
        delay(800.milliseconds)

        if (codes[phone] != code) {
            throw ApiException(
                statusCode = 400,
                message = "Неверный код",
            )
        }

        // удаляем код и возвращаем фейковую пару токенов
        codes.remove(phone)
        return TokenPairDto(
            accessToken = "fake-access-token",
            refreshToken = "fake-refresh-token",
            tokenType = "Bearer",
            expiresInSeconds = 900,
            userId = Uuid.generateV4().toString(),
            )
    }
}