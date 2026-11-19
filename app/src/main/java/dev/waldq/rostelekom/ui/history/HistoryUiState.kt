package dev.waldq.rostelekom.ui.history

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


sealed interface HistoryUiState {

    data class Error(val message: String) : HistoryUiState
    data object Loading : HistoryUiState

    data class NoAccounts(
        val message: String,
    ): HistoryUiState

    data class Content(
        val transactions: List<TransactionUi>,
    ): HistoryUiState
}

/**
 * Одна строка в списке операций.
 *
 * @property amount сумма со знаком: «+500,00 ₽».
 * @property isIncome приход (зелёный) или расход (красный).
 */
data class TransactionUi(
    val isVisible: Boolean,
    val id: String,
    val title: String,
    val date: String,
    val amount: String,
    val isIncome: Boolean,
) {
    companion object {
        fun toRuDate(date: String): String {
            return ZonedDateTime.parse(date)
                .withZoneSameInstant(java.time.ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale("ru")))
        }
    }
}