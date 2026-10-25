package dev.waldq.rostelekom.models

import kotlinx.serialization.Serializable


/**
 * @property id UUID пользователя
 * @property phone номер телефона
 * @property username юзернейм
 * @property firstName имя
 * @property lastName фамилия
 *
 * Body ответа с сервера `/api/users/me`
 */
@Serializable
data class UserProfileDto(
    val id: String, // UUID на самом деле, просто в мобилке оно вряд ли будет использоваться
    val phone: String,
    val username: String?,
    val firstName: String?,
    val lastName: String?
)


/**
 * @param username имя пользователя
 * @param firstName имя
 * @param lastName фамилия
 */
@Serializable
data class UpdateProfileDto(
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)