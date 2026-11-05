package dev.waldq.rostelekom.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.UserApi
import dev.waldq.rostelekom.models.AccountStatus
import dev.waldq.rostelekom.models.TransactionType
import dev.waldq.rostelekom.validation.allowInputPaymentAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            try {
                val user = repository.getProfile()
                val greeting = "Добрый день, ${user.firstName}"
                try {
                    val account = repository.getMyAccount()
                    val transactions = repository.getMyTransactions()
                    _uiState.value = HomeUiState.Content(
                        depositeValue = "",
                        showDeposite = false,
                        selectedItem = 0,
                        greeting = greeting,
                        balance = account.balance.toDouble(),
                        accountNumber = "№ ${account.number.chunked(4).joinToString(" ")}",
                        status = account.status,
                        transactions = transactions.content.map {
                            TransactionUi(
                                id = it.id,
                                title = when (it.type) {
                                    TransactionType.TOP_UP -> "Пополнение баланса"
                                    TransactionType.CHARGE -> "Списание"
                                    TransactionType.REFUND -> "Возврат"
                                },
                                date = it.createdAt,
                                amount = it.amount.toPlainString(),
                                isIncome = it.type != TransactionType.CHARGE,
                            )
                        }.take(3),
                    )
                } catch (e: ApiException) {
                    if (e.statusCode == 404) {   // у нового юзера счёта ещё нет, это не ошибка
                        _uiState.value =
                            HomeUiState.NoAccounts(selectedItem = 0, greeting = greeting)
                    } else throw e
                }
            } catch (e: ApiException) {
                _uiState.value =
                    if (e.statusCode == 401) HomeUiState.SessionExpired else HomeUiState.Error(e.message)
            }



        }
    }

    fun createAccount() {
        viewModelScope.launch {
            val account = repository.openAccount()
            val current = _uiState.value
            if (current is HomeUiState.NoAccounts)
                _uiState.value = HomeUiState.Content(
                    depositeValue = "",
                    showDeposite = false,
                    selectedItem = 0,
                    greeting = current.greeting,
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