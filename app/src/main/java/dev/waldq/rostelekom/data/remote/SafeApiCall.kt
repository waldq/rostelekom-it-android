package dev.waldq.rostelekom.data.remote;


import android.util.Log
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.ProblemDto
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException


suspend fun <T> safeApiCall(block: suspend () -> T): T = try {
    block()
} catch (e: HttpException) {                      // бэк ответил 4xx/5xx
    throw e.toApiException()
} catch (e: IOException) {                        // нет сети, таймаут
    Log.e("Api", "Сетевая ошибка", e)
    throw ApiException(statusCode = 0, message = "Не удалось подключиться к серверу")
} catch (e: SerializationException) {             // вопросы к Потапу и Бажеке
    Log.e("Api", "Сетевая ошибка", e)
    throw ApiException(statusCode = 0, message = "Сервер прислал непонятный ответ")
}

private fun HttpException.toApiException(): ApiException {
    val response = response()

    val body = response?.errorBody()?.string()
    val problem = body?.let { text ->
        runCatching { ApiClient.json.decodeFromString<ProblemDto>(text) }.getOrNull()
    }

    val retryAfter = response?.headers()?.get("Retry-After")?.toLongOrNull() ?: 0

    return ApiException(
        statusCode = code(),
        message = problem?.detail ?: "Ошибка сервера (${code()})",
        retryAfterSeconds = retryAfter,
        reason = problem?.reason,
        fieldErrors = problem?.errors ?: emptyMap(),
    )
}
