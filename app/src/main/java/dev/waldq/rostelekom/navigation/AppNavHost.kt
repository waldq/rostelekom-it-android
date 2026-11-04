package dev.waldq.rostelekom.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.waldq.rostelekom.ui.home.HomeScreen
import dev.waldq.rostelekom.ui.home.ProfileScreen
import dev.waldq.rostelekom.ui.login.LoginRoute
import dev.waldq.rostelekom.ui.otp.OtpRoute

@Composable
fun AppNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Login,
        modifier = modifier
    ) {
        composable<Login> {
            LoginRoute(
                onCodeSent = { phone, challenge -> navController.navigate(
                    route = OtpCode(
                        phone = phone,
                        devCode = challenge.code,
                        resendAfterSeconds = challenge.resendAfterSeconds
                    )
                ) }
            )
        }

        composable<OtpCode> { backStackEntry ->
            val route = backStackEntry.toRoute<OtpCode>()

            OtpRoute(
                phone = route.phone,
                devCode = route.devCode,
                resendAfterSeconds = route.resendAfterSeconds,
                onBack = { navController.popBackStack() },
                onLoggedIn = {
                    navController.navigate(Home) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen {
                navController.navigate(route = Profile) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }

        composable <Profile> {
            ProfileScreen {
                navController.navigate(route = Login) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }
}