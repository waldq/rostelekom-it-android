package dev.waldq.rostelekom

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.navigation.AppNavHost
import dev.waldq.rostelekom.navigation.Home
import dev.waldq.rostelekom.navigation.Login
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RostelekomitandroidTheme {
                val start by produceState<Any?>(initialValue = null) {
                    value = if (AppContainer.tokenStorage.getRefreshToken() != null) Home else Login
                }

                val destination = start
                if (destination == null) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    )
                } else {
                    AppNavHost(startDestination = destination)
                }
            }
        }
    }
}
