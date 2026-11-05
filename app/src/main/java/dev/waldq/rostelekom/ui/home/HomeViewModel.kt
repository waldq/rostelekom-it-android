package dev.waldq.rostelekom.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.models.AccountStatus
import dev.waldq.rostelekom.validation.allowInputPaymentAmount
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
                depositeValue = "0.0",
                showDeposite = false,
                selectedItem = 0,
                greeting = "Добрый день, Ростелекомовцы!",
                balance = 6767.67,
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

    fun updateBalance(value: Double) {
        val current = _uiState.value
        if (current is HomeUiState.Content)
            _uiState.value = current.copy(balance = current.balance + value);
    }

    fun updateDepositeValue(value: String){
        if (!allowInputPaymentAmount(value)) return
        val current = _uiState.value
        if (current is HomeUiState.Content)
            _uiState.value = current.copy(depositeValue = value)
    }

    @SuppressLint("SuspiciousIndentation")
    fun changeShowDeposite(){
        val current = _uiState.value
        if (current is HomeUiState.Content)
        _uiState.value = current.copy(showDeposite = !current.showDeposite);
    }
}