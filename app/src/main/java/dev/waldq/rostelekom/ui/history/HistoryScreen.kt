package dev.waldq.rostelekom.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.ui.theme.Green
import dev.waldq.rostelekom.ui.theme.TransactionsCardColor

@Composable
fun HistoryScreen(
    state: HistoryUiState.Content,
    setVisible: (String) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 24.dp,
                    bottom = 18.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "История операций",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Ваши последние финансовые операции",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (state.transactions.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "↗",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Операций пока нет",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Здесь появятся ваши поступления и списания",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(
                        items = state.transactions,
                        key = { it.id },
                    ) { item ->
                        val accent = if (item.isIncome) {
                            Green
                        } else {
                            MaterialTheme.colorScheme.error
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { setVisible(item.id) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = TransactionsCardColor,
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp,
                            ),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .background(
                                                color = accent.copy(alpha = 0.12f),
                                                shape = CircleShape,
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = if (item.isIncome) "+" else "−",
                                            color = accent,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text(
                                            text = item.title,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = item.date,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }

                                    Text(
                                        text = item.amount,
                                        color = accent,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                if (item.isVisible) {
                                    val uriHandler = LocalUriHandler.current

                                    Button(
                                        onClick = {
                                            uriHandler.openUri(
                                                "https://rutube.ru/video/be9b5aece2911aecc68fa03942e25bac/"
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Text("Скачать чек")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun HistoryRoute () {
    val viewModel: HistoryViewModel = viewModel{ HistoryViewModel(
        userRepository = AppContainer.userRepository,
    ) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is HistoryUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Loading()
        }
        is HistoryUiState.Content -> HistoryScreen(state, setVisible = viewModel::setVisible)
        is HistoryUiState.NoAccounts -> Text(state.message)
        is HistoryUiState.Error -> Text(state.message)   // TODO потом сделать нормальный экран
    }
}


@Composable
private fun Loading(){
    CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = LocalContentColor.current,
        strokeWidth = 2.dp,
    )
}