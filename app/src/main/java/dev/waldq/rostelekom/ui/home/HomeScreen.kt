package dev.waldq.rostelekom.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Column() {
        Text("Вы вошли")
        OutlinedButton(onClick = onLogout) {
            Text("Выйти")
        }
    }
}