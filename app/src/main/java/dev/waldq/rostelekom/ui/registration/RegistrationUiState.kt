package dev.waldq.rostelekom.ui.registration

data class RegistrationUiState(
    val phone: String,
    val registrationToken: String,
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val middleNameError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
) {
    val isButtonEnabled = lastName.isNotBlank() && firstName.isNotBlank() && !isLoading
}