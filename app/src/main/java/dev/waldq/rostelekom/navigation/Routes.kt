package dev.waldq.rostelekom.navigation

import kotlinx.serialization.Serializable

@Serializable object Login
@Serializable data class OtpCode(
    val phone: String,
    val devCode: String? = null,
    val resendAfterSeconds: Long = 60
)
@Serializable object Home

@Serializable object Profile
