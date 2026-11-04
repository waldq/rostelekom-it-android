package dev.waldq.rostelekom.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.models.AccountStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow() // снаружи только неизменяемое состояние

    // данные грузим сразу при открытии экрана
    init {
        load()
    }

    // TODO пока репозиториев для профиля и счёта нет, данные захардкожены.
    //  Потом здесь будут запросы /api/users/me, /api/billing/accounts/me и .../transactions
    private fun load() {
        viewModelScope.launch {
            // имитация задержки, чтобы успевала показаться крутилка
            delay(800.milliseconds)

            _uiState.value = HomeUiState.Content(
                selectedItem = 0,
                greeting = "Добрый день, Ростелекомовцы!",
                balance = String.format(Locale("ru", "RU"), "%,.2f ₽", 6767.67),
                accountNumber = "№ 1234 5678 9012 3456",
                status = AccountStatus.ACTIVE,
                transactions = listOf(
                    TransactionUi("1", "Пополнение баланса", "28 окт., 14:05", "+500,00 ₽", isIncome = true),
                    TransactionUi("2", "Абонентская плата", "25 окт., 00:00", "−650,00 ₽", isIncome = false),
                    TransactionUi("3", "Возврат", "20 окт., 11:42", "+120,00 ₽", isIncome = true),
                ),
            )
        }
    }
}