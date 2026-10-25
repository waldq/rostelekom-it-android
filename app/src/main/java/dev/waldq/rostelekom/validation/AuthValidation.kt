package dev.waldq.rostelekom.validation

private const val RU_NATIONAL_LENGTH = 10
private const val CODE_LENGTH = 6

enum class FieldError {
    EMPTY,
    TOO_SHORT
}

/**
 * Вырезает из строки всё, кроме цифр, и оставляет только 10 символов после 7 или 8
 * Выполняется на каждый введённый символ в строке номера.
 */
fun filterPhone(raw: String): String {
    val digits = raw.filter { it in '0'..'9' }

    val national = if (digits.length > RU_NATIONAL_LENGTH
        && (digits.startsWith("8") || digits.startsWith("7"))) {
        digits.drop(1)
    } else {
        digits
    }

    return national.take(RU_NATIONAL_LENGTH)
}

/**
 * Кнопка отправки номера активна, когда null,
 * иначе при нажатии кнопки пишем ошибку
 */
fun validatePhone(digits: String): FieldError? = when {
    digits.isEmpty() -> FieldError.EMPTY
    digits.length < RU_NATIONAL_LENGTH -> FieldError.TOO_SHORT
    else -> null
}

/**
 * Позволяет ввести в поле кода только цифры.
 * Выполняется на каждый введённый символ в строке кода.
 */
fun filterCode(raw: String): String {
    return raw.filter { it in '0'..'9' }.take(CODE_LENGTH)
}

/**
 * Кнопка отправки кода активна, когда null,
 * иначе при нажатии кнопки пишем ошибку
 */
fun validateCode(raw: String): FieldError? = when {
    raw.isEmpty() -> FieldError.EMPTY
    raw.length != CODE_LENGTH -> FieldError.TOO_SHORT
    else -> null
}