package dev.waldq.rostelekom.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.waldq.rostelekom.models.AccountStatus
import dev.waldq.rostelekom.ui.theme.BalanceGradient
import dev.waldq.rostelekom.ui.theme.Green
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme
import dev.waldq.rostelekom.ui.theme.White

/**
 * Главный экран. Сам создаёт ViewModel, поэтому в навигации ничего менять не нужно.
 *
 */
@Composable
fun HomeScreen(onProfile: () -> Unit) {
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        HomeUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is HomeUiState.Content -> HomeContent(state, onProfile)
    }
}

/**
 * Один LazyColumn: приветствие, карточка баланса, кнопка пополнения, последние операции.
 */
@Composable
private fun HomeContent(content: HomeUiState.Content, onProfile: () -> Unit) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NavBarItems.entries.forEachIndexed { index, items ->
                        NavigationBarItem(
                            selected = content.selectedItem == index,
                            onClick = {
                                when (items) {
                                    NavBarItems.PROFILE -> onProfile()
                                    else -> {}
                                }
                            } ,
                            icon = { Text(items.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(innerPadding),
            contentPadding = PaddingValues(13.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text(
                        text = "ЛИЧНЫЙ КАБИНЕТ",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.6.sp),
                    )
                    Text(
                        text = content.greeting,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }

            item { BalanceCard(content) }

            item {
                Button(
                    onClick = { /* TODO открыть пополнение */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text("Пополнить баланс")
                }
            }

            item { TransactionsCard(content.transactions) }
        }
    }
}

/**
 * enum для NavBar
 * TODO доделать навигацию
 * возможно можно вынести куда-нибудь, чтобы в истории тарифах и профиле переиспользовать
 */
public final enum class NavBarItems(val title: String) {
    MAIN("Главная"),
    HISTORY("История"),
    RATES("Тарифы"),
    PROFILE("Профиль"),
}



/**
 * Карточка баланса с градиентом: подпись и статус сверху, баланс, номер счёта.
 */
@Composable
private fun BalanceCard(content: HomeUiState.Content) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BalanceGradient)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Текущий баланс",
                color = White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = content.statusText,
                modifier = Modifier
                    .background(White.copy(alpha = 0.16f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                color = White,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Text(
            text = content.balance,
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp),
            color = White,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 36.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.ExtraBold,
            ),
        )

        Text(
            text = "Лицевой счёт  ${content.accountNumber}",
            color = White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/**
 * Белая карточка «Последние операции». Если операций нет, внутри только надпись.
 */
@Composable
private fun TransactionsCard(transactions: List<TransactionUi>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp, // иначе Material подмешает в белый фиолетовый оттенок
        shadowElevation = 6.dp,
    ) {
        if (transactions.isEmpty()) {
            Text(
                text = "Операций пока нет",
                modifier = Modifier.padding(24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Последние операции",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                transactions.forEachIndexed { index, transaction ->
                    if (index > 0) HorizontalDivider()
                    TransactionRow(transaction)
                }
            }
        }
    }
}

/**
 * Строка операции: иконка со знаком, название с датой и сумма. Приход зелёный, расход красный.
 */
@Composable
private fun TransactionRow(transaction: TransactionUi) {
    val accent = if (transaction.isIncome) Green else MaterialTheme.colorScheme.error

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(accent.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (transaction.isIncome) "+" else "−",
                color = accent,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            )
            Text(
                text = transaction.date,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Text(
            text = transaction.amount,
            color = accent,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp")
@Composable
private fun HomeContentPreview() {
    RostelekomitandroidTheme {
        HomeContent(
            content = HomeUiState.Content(
                selectedItem = 0,
                greeting = "Добрый день, Владимир!",
                balance = "1 250,00 ₽",
                accountNumber = "№ 7701 2345 6789",
                status = AccountStatus.ACTIVE,
                transactions = listOf(
                    TransactionUi("1", "Пополнение баланса", "28 окт., 14:05", "+500,00 ₽", isIncome = true),
                    TransactionUi("2", "Абонентская плата", "25 окт., 00:00", "−650,00 ₽", isIncome = false),
                ),
            ),
            onProfile = {},
        )
    }
}