package dev.waldq.rostelekom.data.remote;


import dev.waldq.rostelekom.data.repository.ApiException
import retrofit2.HttpException
import java.io.IOException


suspend fun <T> safeApiCall(block: suspend () -> T): T = try {
    block()
} catch (e: HttpException) {                      // бэк ответил 4xx/5xx
    throw ApiException(
        // TODO поменять исключения
        statusCode = e.code(),
        message = e.message(),                    // TODO если бэк присылает текст ошибки в JSON - разобрать его здесь
        retryAfterSeconds = e.response()?.headers()?.get("Retry-After")?.toLongOrNull() ?: 0,
    )
} catch (e: IOException) {                        // нет сети, таймаут
    throw ApiException(statusCode = 0, message = "Не удалось подключиться к серверу")
}
