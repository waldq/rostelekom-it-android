package dev.waldq.rostelekom.models

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


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
    val id: Uuid,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
)