package dev.waldq.rostelekom.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import dev.waldq.rostelekom.R
import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme

@Composable
fun ProfileRoute(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel { ProfileViewModel(
        userRepository = AppContainer.userRepository,
        billingRepository = AppContainer.billingRepository,
    ) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        onLogout = onLogout,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    if (uiState is ProfileUiState.Content) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Профиль",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
        ) { innerPadding ->
            val isLoggingOut = remember { mutableStateOf(false) }
            if (isLoggingOut.value) {
                AlertDialog(
                    onDismissRequest = { isLoggingOut.value = false },
                    title = { Text("Выйти из аккаунта?") },
                    confirmButton = {
                        TextButton(onClick = {
                            isLoggingOut.value = false
                            onLogout()
                        }) {
                            Text("Да")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            isLoggingOut.value = false
                        }) {
                            Text("Нет")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            }

            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
                ) {
                    Row(modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5E7EB))
                                .border(3.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_profile),
                                contentDescription = null,
                                tint = Color(0xFF717680),
                                modifier = Modifier.size(36.dp),
                            )
                        }
                        Spacer(Modifier.width(20.dp))
                        Text(
                            text = "${uiState.firstName} ${uiState.lastName}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                Card(modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
                ) {
                    ListItem(
                        headlineContent = { Text(uiState.phone) },
                        modifier = Modifier.fillMaxWidth(),
                        overlineContent = { Text("Телефон") },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.ic_phone),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }

                Card(modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
                ) {
                    ListItem(
                        headlineContent = {
                            TextButton(
                                onClick = { isLoggingOut.value = true },
                                colors = ButtonDefaults.buttonColors().copy(
                                    contentColor = MaterialTheme.colorScheme.error,
                                    containerColor = Color.Transparent,
                                    )
                            ) {
                                Text(
                                    text = "Выйти",
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.ic_logout),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp")
@Composable
private fun ProfileScreenPreview() {
    RostelekomitandroidTheme {
        ProfileScreen(
            uiState = ProfileUiState.Content(
                phone = "+7 999 123 45 67",
                firstName = "Иван",
                lastName = "Иванов",
                middleName = "Иванович",
                accountNumber = "1234 5678 9012 3456"
            )
        ) {}
    }
}

//@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp")
//@Composable
//fun HomeScreenPreview() {
//    RostelekomitandroidTheme{
//        HomeScreen {
//            {}
//        }
//    }
//}