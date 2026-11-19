package dev.waldq.rostelekom.ui.services

import dev.waldq.rostelekom.models.PageDto
import dev.waldq.rostelekom.models.TariffCategory
import dev.waldq.rostelekom.models.TariffDto

sealed class TariffsUiState {
    /**
     * 
     */
    data class Content(
        val tariffsByCategory: Map<TariffCategory, List<TariffDto>>
    ) : TariffsUiState()

    /**
     * @property message текст ошибки
     */
    data class Error(val message: String) : TariffsUiState()

    data object Loading : TariffsUiState()
}