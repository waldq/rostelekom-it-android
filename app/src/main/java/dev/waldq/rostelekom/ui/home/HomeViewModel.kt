package dev.waldq.rostelekom.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.UserApi
import dev.waldq.rostelekom.models.AccountStatus
import dev.waldq.rostelekom.models.TransactionType
import dev.waldq.rostelekom.validation.allowInputPaymentAmount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val repository: UserApi
) : ViewModel() {
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
            val user = repository.getProfile()
            _uiState.value = HomeUiState.Content(
                depositeValue = "0.0",
                showDeposite = false,
                selectedItem = 0,
                greeting = "Добрый день, ${user.firstName}",
                transactions = emptyList(),
            )
            val current = _uiState.value
            try {
                val account  = repository.getMyAccount()
                val transactions = repository.getMyTransactions()
                if (current is HomeUiState.Content)
                    _uiState.value = current.copy(
                        balance = account.balance.toDouble(),
                        accountNumber = "№ ${account.number.chunked(4).joinToString(" ")}",
                        status = account.status,
                        transactions = // Можно будет использовать для истории
                              transactions.content.map {it -> TransactionUi(
                                  id = it.id,
                                  title = when (it.type){
                                      TransactionType.TOP_UP -> "Пополнение баланса"
                                      TransactionType.CHARGE -> "Списания"
                                      TransactionType.REFUND -> "Возварт"
                                  },
                                  date = it.createdAt,
                                  amount = it.amount.toString(),
                                  isIncome = if (it.type == TransactionType.CHARGE) false else true,
                              ) }.take(3)
                    )
            }catch (e: HttpException) {
                val httpCode = e.code()
                if (httpCode == 404)
                    _uiState.value = HomeUiState.NoAccounts(selectedItem = 0, greeting = "Добрый день, ${user.firstName}")
            }



        }
    }

    fun createAccount() {
        viewModelScope.launch {
            val account = repository.openAccount()
            val current = _uiState.value
            if (current is HomeUiState.Content)
                _uiState.value = current.copy(

                    balance = account.balance.toDouble(),
                    accountNumber = "№ ${account.number.chunked(4).joinToString(" ")}",
                    status = AccountStatus.ACTIVE,
                    transactions = emptyList()
                    )
        }
    }

    fun updateBalance(value: Double) {
        val current = _uiState.value
        if (current is HomeUiState.Content && current.balance != null)
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