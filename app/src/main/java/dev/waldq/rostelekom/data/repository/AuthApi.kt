package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.models.OtpChallengeDto
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.models.OtpVerifyDto
import dev.waldq.rostelekom.models.RefreshTokenDto
import dev.waldq.rostelekom.models.RegisterDto
import dev.waldq.rostelekom.models.VerifyResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Интерфейс репозитория, работающий с запросами на авторизацию.
 *
 * С ним взаимодействует ViewModel экрана логина,
 * чтобы UI не видел непосредственного кода репозитория.
 */
interface AuthApi {
    /**
     * Функция для запроса на получение OTP кода.
     *
     * Отправляет запрос на `/api/auth/otp/request` в виде [OtpRequestDto].
     *
     * @param body это Dto, где номер телефона в формате +79991234567.
     */
    @POST("api/auth/otp/request")
    suspend fun requestCode(@Body body: OtpRequestDto): OtpChallengeDto

    /**
     * Функция для отправки кода и получения пары токенов.
     *
     * Отправляет запрос на `/api/auth/otp/verify` в виде [OtpVerifyDto]
     *
     * @param body это номер телефона это OTP код
     */
    @POST("api/auth/otp/verify")
    suspend fun verifyCode(@Body body: OtpVerifyDto): VerifyResponseDto

    @POST("api/auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenDto): VerifyResponseDto

    @POST("api/auth/register")
    suspend fun registerAccount(@Body body: RegisterDto): VerifyResponseDto
}