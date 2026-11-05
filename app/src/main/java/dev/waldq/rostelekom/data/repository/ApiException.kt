package dev.waldq.rostelekom.data.repository

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
) : Exception(message)