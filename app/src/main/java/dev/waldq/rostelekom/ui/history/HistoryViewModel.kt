package dev.waldq.rostelekom.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.UserApi
import dev.waldq.rostelekom.models.TransactionType
import dev.waldq.rostelekom.ui.history.TransactionUi.Companion.toRuDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel (
    private val userRepository: UserApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow() // снаружи только неизменяемое состояние

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            try {
                val transactions = userRepository.getMyTransactions() // транзакции для отображения
                _uiState.value = HistoryUiState.Content(
                    transactions = transactions.content.map {
                        TransactionUi(
                            isVisible = false,
                            id = it.id,
                            title = it.type.title,
                            date = toRuDate(it.createdAt),
                            amount = it.amount.toPlainString(),
                            isIncome = it.type != TransactionType.CHARGE,
                        )
                    }.take(10),
                )
            } catch (e: ApiException) {
                if (e.statusCode == 404) {   // у нового юзера счёта ещё нет, это не ошибка
                    _uiState.value =
                        HistoryUiState.NoAccounts(message = "Истории транзакций нет, создайте лицевой счёт")
                } else {
                    _uiState.value =
                        HistoryUiState.Error("Ошибка какая-то")
                }
            }
        }
    }

    fun setVisible(id: String) {
        _uiState.update { state ->
            if (state !is HistoryUiState.Content) return@update state

            state.copy(
                transactions = state.transactions.map { transaction ->
                    if (transaction.id == id) {
                        transaction.copy(isVisible = !transaction.isVisible)
                    } else {
                        transaction.copy(isVisible = false)
                    }
                }
            )
        }
    }
}