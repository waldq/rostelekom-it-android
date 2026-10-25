package dev.waldq.rostelekom.models

import kotlinx.serialization.Serializable


/**
 *  @property phone отформатированный номер телефона.
 *
 *  Сериализуется в JSON, ключи по умолчанию имеют именя полей класса.
 *
 *  Body запроса на сервер. `/api/auth/otp/request`
 */
@Serializable
data class OtpRequestDto(val phone: String)


/**
 *  @param phone отформатированный номер телефона.
 *  @param code OTP код.
 *
 *  Body запроса на сервер. `/api/auth/otp/verify`
 */
@Serializable
data class OtpVerifyDto(val phone: String, val code: String)


/**
 *  @property refreshToken refresh токен для запроса на получение пары токенов и на выход (отзыв рефреша).
 *
 *  Body запроса на сервер. `/api/auth/refresh` и `/api/auth/logout`
 */
@Serializable
data class RefreshTokenDto(val refreshToken: String)


/**
 * @property phone номер телефона
 * @property code код // TODO убрать в проде
 * @property expiresInSeconds время истечения кода
 * @property resendAfterSeconds время, через которое можно повторно запросить код
 *
 * Body ответа с сервера. `/api/auth/otp/request`
 */
@Serializable
data class OtpChallengeDto(
    val phone: String,
    val code: String? = null,
    val expiresInSeconds: Long,
    val resendAfterSeconds: Long
)


/**
 * @property accessToken access токен
 * @property refreshToken refresh токен
 * @property tokenType тип токена
 * @property expiresInSeconds время истечения токена
 * @property userId UUID пользователя
 *
 * Body ответа с сервера `/api/auth/otp/verify`
 */
@Serializable
data class TokenPairDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
    val userId: String // UUID на самом деле, просто в мобилке оно вряд ли будет использоваться
)