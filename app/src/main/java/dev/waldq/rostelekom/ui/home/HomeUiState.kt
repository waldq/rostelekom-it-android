package dev.waldq.rostelekom.ui.home

import dev.waldq.rostelekom.models.AccountStatus

/**
 * Состояние главного экрана: либо грузимся, либо показываем данные.
 */
sealed interface HomeUiState {
    data object Loading : HomeUiState

    /**
     * @property greeting приветствие целиком: «Добрый день, Владимир!».
     * @property balance баланс уже строкой: «1 250,00 ₽».
     * @property accountNumber номер лицевого счёта: «№ 7701 2345 6789».
     * @property status статус счёта.
     * @property transactions последние операции, новые первыми.
     */
    data class Content(
        val selectedItem: Int,
        val greeting: String,
        val balance: String,
        val accountNumber: String,
        val status: AccountStatus,
        val transactions: List<TransactionUi>,
    ) : HomeUiState {
        val statusText = when (status) {
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