package dev.waldq.rostelekom.ui.registration

data class RegistrationUiState(
    val phone: String,
    val registrationToken: String,
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val isFirstNameEnabled: Boolean = true,
    val isLastNameEnabled: Boolean = true,
    val isMiddleNameEnabled: Boolean = true,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val middleNameError: String? = null,
    val isButtonEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
)