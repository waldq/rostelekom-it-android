package dev.waldq.rostelekom.ui.home

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.models.AccountStatus
import dev.waldq.rostelekom.ui.components.AmountVisualTransformation
import dev.waldq.rostelekom.ui.theme.BalanceGradient
import dev.waldq.rostelekom.ui.theme.Green
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme
import dev.waldq.rostelekom.ui.theme.White
import java.util.Locale
import androidx.core.net.toUri

/**
 * Главный экран. Сам создаёт ViewModel, поэтому в навигации ничего менять не нужно.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UseKtx", "QueryPermissionsNeeded")
@Composable
fun HomeScreen(
    content: HomeUiState.Content,
    onProfile: () -> Unit,
    onChangeShowDeposit: () -> Unit,
    onUpdateDepositValue: (String) -> Unit,
    onPay: (String) -> Unit,
    ) {
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
        if (content.showDeposite) {
            ModalBottomSheet(
                onDismissRequest = onChangeShowDeposit,
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
                        .imePadding(), // чтобы клавиатура не закрывала кнопку
                ) {
                    Text(
                        text = "Пополнить баланс",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Деньги поступят на лицевой счёт сразу после подтверждения.",
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    OutlinedTextField(
                        value = content.depositeValue, //String.format(Locale("ru", "RU"), "%,.2f ₽", content.depositeValue),
                        onValueChange = onUpdateDepositValue,
                        visualTransformation = AmountVisualTransformation,
                        modifier = Modifier.fillMaxWidth(),
                        label = {Text("Сумма")},
                        suffix = { Text("₽") },
                        textStyle = MaterialTheme.typography.titleLarge,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )

                    Row(
                        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(100.0 to "+100 ₽", 500.0 to "+500 ₽", 1000.0 to "+1 000 ₽").forEach { (value, label) ->
                            Button(
                                onClick = { onUpdateDepositValue((value + content.depositeAmount).toString()) }
                            ) {
                                Text(label)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onPay(content.depositeValue)
                            onUpdateDepositValue(("0.0"))
                            onChangeShowDeposit()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = content.depositeAmount > 1e-9,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    ) {
                        Text(if (content.depositeAmount > 1e-9) "Пополнить на ${content.depositeValue}" else "Введите сумму пополнения")
                    }
                }

            }
        }
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
                    onClick = onChangeShowDeposit,
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
 * Экран «У вас пока нет лицевого счёта». Показывается, когда бэк на запрос счёта ответил 404.
 * Состояния внутри нет, только вёрстка.
 *
 * @param onOpenAccount кнопка «Открыть счёт» (запрос `POST api/billing/accounts`).
 */
@Composable
fun NoAccountScreen(
    onOpenAccount: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(16.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp, // иначе Material подмешает в белый фиолетовый оттенок
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // квадрат со знаком рубля
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "₽",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "У вас пока нет лицевого счёта",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Откройте его, чтобы пополнять баланс и смотреть историю операций.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onOpenAccount,
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text("Открыть счёт")
                }
            }
        }
    }
}


@Composable
fun CreateAccount(content: HomeUiState.NoAccounts, viewModel: HomeViewModel) {
   Scaffold() { innerPadding ->
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
            item{ NoAccountScreen(viewModel::createAccount) }

        }
    }
}

@Composable
fun HomeRoute(onProfile: () -> Unit, onSessionExpired: () -> Unit) {
    val viewModel: HomeViewModel = viewModel{ HomeViewModel(
        userRepository = AppContainer.userRepository,
        billingRepository = AppContainer.billingRepository,
    ) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.load() }

    when (val state = uiState) {
        HomeUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Loading()
        }
        is HomeUiState.Content -> HomeScreen(
            content = state,
            onProfile = onProfile,
            onChangeShowDeposit = viewModel::changeShowDeposit,
            onUpdateDepositValue = viewModel::updateDepositValue,
            onPay = { amount -> viewModel.createPayment(amount){ url ->
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                } catch (e: ActivityNotFoundException) {
                    Log.e("Home", "не могу открыть $url")
                }
            } },
        )
        is HomeUiState.NoAccounts -> CreateAccount(state, viewModel)
        is HomeUiState.SessionExpired -> LaunchedEffect(Unit) { onSessionExpired() }
        is HomeUiState.Error -> Text(state.message)   // потом сделать нормальный экран
    }
}

@SuppressLint("UseKtx", "QueryPermissionsNeeded")
@Composable
private fun Loading(){
    val context = LocalContext.current
    var text = "Показать баланс"
    Button(onClick = {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:*102#")
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            text = "Установите приложение для набора номера"
        }
    }) {
        Text(text)
    }
}

/**
 * Один LazyColumn: приветствие, карточка баланса, кнопка пополнения, последние операции.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(content: HomeUiState.Content, viewModel: HomeViewModel, onProfile: () -> Unit) {

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
            text = String.format(Locale("ru", "RU"), "%,.2f ₽", content.balance),
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
        HomeScreen(
            content = HomeUiState.Content(
                depositeValue = "0.0",
                showDeposite = false,
                selectedItem = 0,
                greeting = "Добрый день, Владимир!",
                balance = 6767.67,
                accountNumber = "№ 7701 2345 6789",
                status = AccountStatus.ACTIVE,
                transactions = listOf(
                    TransactionUi(
                        "1",
                        "Пополнение баланса",
                        "28 окт., 14:05",
                        "+500,00 ₽",
                        isIncome = true
                    ),
                    TransactionUi(
                        "2",
                        "Абонентская плата",
                        "25 окт., 00:00",
                        "−650,00 ₽",
                        isIncome = false
                    ),
                ),
            ),
            onProfile = {},
            onChangeShowDeposit = {},
            onUpdateDepositValue = {},
            onPay = {},
        )
    }
}