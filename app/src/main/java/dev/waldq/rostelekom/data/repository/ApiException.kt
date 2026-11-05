package dev.waldq.rostelekom.data.repository

import kotlinx.serialization.Serializable

/**
 * Класс исключения для ошибок работы с бэком.
 *
 * @property statusCode HTTP-код ошибки
 * @property message текст ошибки
 * @property retryAfterSeconds сколько секунд нужно подождать до повторной попытки, если есть у ошибки.
 */
class ApiException(
    val statusCode: Int,
    override val message: String,
    val retryAfterSeconds: Long = 0,
    val reason: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
) : Exception(message)

@Serializable
data class ProblemDto(
    val detail: String? = null,
    val reason: String? = null,
    val attemptsLeft: Int? = null,
    val errors: Map<String, String>? = null,
)