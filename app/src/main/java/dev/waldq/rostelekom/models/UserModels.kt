package dev.waldq.rostelekom.models

import kotlinx.serialization.Serializable


/**
 * @property id UUID пользователя
 * @property phone номер телефона
 * @property firstName имя
 * @property lastName фамилия
 * @property middleName отчество
 *
 * Body ответа с сервера `/api/users/me`
 */
@Serializable
data class UserProfileDto(
    val id: String, // UUID на самом деле, просто в мобилке оно вряд ли будет использоваться
    val phone: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
)