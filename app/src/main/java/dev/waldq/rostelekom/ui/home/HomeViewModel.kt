package dev.waldq.rostelekom.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.collection.emptyLongSet
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.BillingApi
import dev.waldq.rostelekom.data.repository.UserApi
import dev.waldq.rostelekom.models.AccountStatus
import dev.waldq.rostelekom.models.CreatePaymentDto
import dev.waldq.rostelekom.models.PaymentStatus
import dev.waldq.rostelekom.models.TransactionType
import dev.waldq.rostelekom.validation.allowInputPaymentAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val userRepository: UserApi,
    private val billingRepository: BillingApi,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow() // снаружи только неизменяемое состояние

    private var key = UUID.randomUUID().toString()

    // данные грузим сразу при открытии экрана
    init {
        load()
    }

    // TODO пока репозиториев для профиля и счёта нет, данные захардкожены.
    //  Потом здесь будут запросы /api/users/me, /api/billing/accounts/me и .../transactions
    fun load() {
        viewModelScope.launch {
            try {
                val user = userRepository.getProfile()
                val greeting = "Добрый день, ${user.firstName}"
                try {
                    val account = userRepository.getMyAccount()
                    val transactions = userRepository.getMyTransactions() // транзакции для отображения
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
            val account = userRepository.openAccount()
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
        viewModelScope.launch {
            val account = userRepository.getMyAccount()
            val current = _uiState.value
            if (current is HomeUiState.Content && current.balance != null)
                _uiState.value = current.copy(balance = account.balance.toDouble());
        }
    }

    fun updateDepositValue(value: String){
        if (!allowInputPaymentAmount(value)) return
        val current = _uiState.value
        if (current is HomeUiState.Content)
            _uiState.value = current.copy(depositeValue = value)
    }

    @SuppressLint("SuspiciousIndentation")
    fun changeShowDeposit(){
        val current = _uiState.value
        if (current is HomeUiState.Content)
            _uiState.value = current.copy(showDeposite = !current.showDeposite);
    }

    fun createPayment(amount: String, onUrl: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val current = _uiState.value
                if (current is HomeUiState.Content) {
                    val last = billingRepository.getMyPayments().content.firstOrNull()
                    if (last != null && last.status == PaymentStatus.PENDING && last.amount == amount.toDouble()) {
                        last.confirmationUrl?.let(onUrl)
                    } else {
                        val payment = billingRepository.createPayment(
                            key = UUID.randomUUID().toString(),
                            body = CreatePaymentDto(amount)
                        )
                        payment.confirmationUrl?.let(onUrl)
                    }
                }
            } catch (e: ApiException) {
                Log.e("Home", "createPayment: ${e.statusCode} ${e.message}")
            }
        }
    }
}