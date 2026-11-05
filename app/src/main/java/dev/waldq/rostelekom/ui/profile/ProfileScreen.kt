package dev.waldq.rostelekom.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    OutlinedButton(onClick = onLogout) {
                        Text("Выйти")
                    }
                }
            )
        }
    ) {
            innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("Профиль")

        }
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