package dev.waldq.rostelekom.ui.otp

import dev.waldq.rostelekom.validation.validateCode

data class OtpUiState(
    val phone: String,
    val devCode: String? = null,
    val code: String = "",
    val error: String? = null,
    val retryAfterSeconds: Long = 0,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val registrationToken: String? = null,
) {
    private val minutes = retryAfterSeconds / 60
    private val seconds = retryAfterSeconds % 60

    val resendTimeText = "%d:%02d".format(minutes, seconds)
    val isButtonEnabled = validateCode(code) == null
}
