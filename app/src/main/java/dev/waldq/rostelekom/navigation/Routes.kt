package dev.waldq.rostelekom.navigation

import androidx.annotation.DrawableRes
import dev.waldq.rostelekom.R
import kotlinx.serialization.Serializable

@Serializable object Login
@Serializable data class OtpCode(
    val phone: String,
    val devCode: String? = null,
    val resendAfterSeconds: Long = 60
)

@Serializable data class Registration(
    val phone: String,
    val registrationToken: String,
)
@Serializable object Home

@Serializable object History

@Serializable object Tariffs

@Serializable object Profile

/**
 * enum для NavBar
 */
enum class NavBarItems(
    val title: String,
    val route: Any,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int = icon,
) {
    MAIN("Главная", Home, R.drawable.ic_nav_home, R.drawable.ic_nav_home_filled),
    HISTORY("История", History, R.drawable.ic_nav_history),
    TARIFFS("Тарифы", Tariffs, R.drawable.ic_nav_tariffs),
    PROFILE("Профиль", Profile, R.drawable.ic_nav_profile),
}