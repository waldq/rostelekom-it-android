package dev.waldq.rostelekom.ui.home

import dev.waldq.rostelekom.models.AccountStatus

/**
 * Состояние главного экрана: либо грузимся, либо показываем данные.
 */
sealed interface HomeUiState {

    data object SessionExpired : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Loading : HomeUiState

    data class NoAccounts(
        val greeting: String,
    ): HomeUiState

    /**
     * @property greeting приветствие целиком: «Добрый день, Владимир!».
     * @property balance баланс уже строкой: «1 250,00 ₽».
     * @property accountNumber номер лицевого счёта: «№ 7701 2345 6789».
     * @property status статус счёта.
     * @property transactions последние операции, новые первыми.
     */
    data class Content(
        val depositeValue: String,
        val showDeposite: Boolean,
        val selectedItem: Int,
        val greeting: String,
        val balance: Double? = null,
        val accountNumber: String? = null,
        val status: AccountStatus? = null,
        val transactions: List<TransactionUi>,
    ) : HomeUiState {
        val depositeAmount: Double
            get() = depositeValue?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
        val statusText = when (status) {
            null -> "Не существует"
            AccountStatus.ACTIVE -> "Активен"
            AccountStatus.BLOCKED -> "Заблокирован"
            AccountStatus.CLOSED -> "Закрыт"
        }
    }
}

/**
 * Одна строка в списке операций.
 *
 * @property amount сумма со знаком: «+500,00 ₽».
 * @property isIncome приход (зелёный) или расход (красный).
 */
data class TransactionUi(
    val id: String,
    val title: String,
    val date: String,
    val amount: String,
    val isIncome: Boolean,
)