package dev.waldq.rostelekom.ui.login

import dev.waldq.rostelekom.models.OtpChallengeDto
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.validation.validatePhone

/**
 * Data класс состояния, чтобы копировать с новыми полями, а не изменять существующие.
 * Все поля так или иначе используются в [LoginScreen].
 *
 * @property phone номер телефона (10 цифр).
 * @property error текст ошибки под полем ввода номера.
 * @property isLoading крутилка загрузки в кнопке.
 * @property retryAfterSeconds сколько секунд нужно подождать до повторной попытки.
 * @property challenge ответ на запрос на получение OTP кода. Если есть - переходим на страницу ввода кода.
 */
data class LoginUiState(
    val phone: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val retryAfterSeconds: Long = 0,
    val challenge: OtpChallengeDto? = null,
) {
    val isButtonEnabled = validatePhone(phone) == null && retryAfterSeconds == 0L
    val buttonText = if (retryAfterSeconds > 0) {
        "Повторить через $retryAfterSeconds сек."
    } else {
        "Получить код"
    }
}
