package dev.waldq.rostelekom.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.ui.home.HomeRoute
import dev.waldq.rostelekom.ui.profile.ProfileScreen
import dev.waldq.rostelekom.ui.login.LoginRoute
import dev.waldq.rostelekom.ui.otp.OtpRoute
import dev.waldq.rostelekom.ui.registration.RegistrationRoute

import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    startDestination: Any,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val showBottomBar = NavBarItems.entries.any { destination?.hasRoute(it.route::class) == true }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // нужно, чтобы не было двойных отступов, например, от строки состояния
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavBarItems.entries.forEach { item ->
                        val selected = destination?.hasRoute(item.route::class) == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo<Home> { saveState = true } // не позволяет копиться экранам в стеке и сохраняет состояние экрана
                                    launchSingleTop = true // повторное нажатие на вкладку не откроет её снова поверх
                                    restoreState = true // восстанавливает состояние экрана из saveState
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(if (selected) item.selectedIcon else item.icon),
                                    contentDescription = null,
                                )
                            },
                            label = { Text(item.title) },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding), // нужно, чтобы не было двойных отступов, например, от строки состояния
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
                    onRegistrationRequired = { token ->
                        navController.navigate(Registration(phone = route.phone, registrationToken = token)) {
                            popUpTo<OtpCode> { inclusive = true }
                        }
                    },
                    onLoggedIn = {
                        navController.navigate(Home) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable<Registration> { backStackEntry ->
                val route = backStackEntry.toRoute<Registration>()

                RegistrationRoute(
                    phone = route.phone,
                    registrationToken = route.registrationToken,
                    onBack = { navController.popBackStack() },
                    onSessionExpired = {
                        navController.navigate(Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onLoggedIn = {
                        navController.navigate(Home) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable<Home> {
                HomeRoute (
                    onSessionExpired = {
                        scope.launch {
                            AppContainer.tokenStorage.clear()
                            navController.navigate(Login) { popUpTo(navController.graph.id) { inclusive = true } }
                        }
                    },
                )
            }

            composable<History> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "История - TODO")
                }
            }

            composable<Tariffs> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Тарифы - TODO")
                }
            }

            composable <Profile> {
                ProfileScreen {
                    scope.launch {
                        AppContainer.tokenStorage.clear()
                        navController.navigate(Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}