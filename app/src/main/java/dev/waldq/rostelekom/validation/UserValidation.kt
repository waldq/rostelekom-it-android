package dev.waldq.rostelekom.validation

private const val MIN_USERNAME_LEN = 3
private const val MAX_USERNAME_LEN = 20
private const val MAX_NAME_LEN = 100

enum class ProfileDataError {
    TOO_SHORT,
    TOO_LONG,

}

/**
 * Не даёт ввести пустые символы (пробелы, табуляции и тд) и обрезает имя пользователя до 20 символов
 * Выполняется на каждый введённый символ в строке имени пользователя.
 */
fun filterUsername(raw: String): String {
    return raw.filter { !it.isWhitespace() }.take(MAX_USERNAME_LEN)
}


/**
 * Позволяет вводить только буквы.
 * Выполняется на каждый введённый символ в строке имени или фамилии.
 */
fun filterName(raw: String): String {
    return raw.filter { it.isLetter() }.take(MAX_NAME_LEN)
}

/**
 * Проверяет, что хотя бы одно поле заполнено.
 *
 * Имя и фамилия не проверяются отдельно, это делает фильтр
 * В целом форма сначала проверяет каждое отдельное поле
 * - при validateUsername null и !isAnyFieldFilled -> кнопка не активна
 * - при хотя бы одном не null - показываем ошибки
 * - иначе (- при validateUsername null и isAnyFieldFilled, это уже не обязательно) -> отправляем на сервер
 */
fun isAnyFieldFilled(username: String, firstName: String, lastName: String): Boolean {
    return username.isNotBlank() || firstName.isNotBlank() || lastName.isNotBlank()
}

/**
 * Проверяет, что длина имени пользователя подходящая.
 */
fun validateUsername(username: String): ProfileDataError? {
    val len = username.length
    return when {
        len in 1..<MIN_USERNAME_LEN -> ProfileDataError.TOO_SHORT
        len > MAX_USERNAME_LEN -> ProfileDataError.TOO_LONG
        else -> null
    }
}

