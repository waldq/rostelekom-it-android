package dev.waldq.rostelekom.ui.profile


sealed class ProfileUiState {
    /**
     * @property phone номер телефона
     * @property firstName имя
     * @property lastName фамилия
     * @property middleName отчество
     * @property accountNumber номер лицевого счёта
     */
    data class Content(
        val phone: String,
        val firstName: String,
        val lastName: String,
        val middleName: String?,
        val accountNumber: String,
    ) : ProfileUiState()

    /**
     * @property message текст ошибки
     */
    data class Error(val message: String) : ProfileUiState()

    data object Loading : ProfileUiState()
}
