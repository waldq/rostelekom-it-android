package dev.waldq.rostelekom.validation

import java.math.BigDecimal

private const val MAX_DECIMAL_DIGITS = 2

enum class AmountError {
    INVALID_FORMAT,
    NOT_POSITIVE

}

/**
 * Разрешает вводить только числа с двумя цифрами после единственной запятой или точки.
 * Выполняется на каждый введённый символ в стркое с суммой пополнения.
 */
fun allowInputPaymentAmount(raw: String): Boolean {
    if (!raw.all { it.isDigit() || it == ',' || it == '.' }) return false

    val replaced = raw.replace(",", ".")

    val afterFirstDivider = if (replaced.contains('.')) {
        replaced.substringAfter('.')
    } else {
        return true
    }

    val separators = afterFirstDivider.count { it == '.' }
    return !(separators > 0 || afterFirstDivider.length > MAX_DECIMAL_DIGITS)
}

/**
 * Кнопка пополнения активна, когда null,
 * иначе при нажатии кнопки пишем ошибку
 */
fun validatePayment(raw: String): AmountError? {
    val dec = raw.replace(",", ".").toBigDecimalOrNull()
    return when {
        dec == null -> AmountError.INVALID_FORMAT
        dec <= BigDecimal.ZERO -> AmountError.NOT_POSITIVE
        else -> null
    }
}