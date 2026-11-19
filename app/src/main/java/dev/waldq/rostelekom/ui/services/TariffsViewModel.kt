package dev.waldq.rostelekom.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.TariffsApi
import dev.waldq.rostelekom.models.TariffCategory
import dev.waldq.rostelekom.models.TariffDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TariffsViewModel(
    val tariffsRepository: TariffsApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<TariffsUiState>(TariffsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val tariffs = mutableMapOf<TariffCategory, List<TariffDto>>()
                TariffCategory.entries.forEach {
                    val pages = tariffsRepository.getTariffs(it)
                    tariffs[it] = pages.content
                }
                _uiState.value = TariffsUiState.Content(
                    tariffsByCategory = tariffs.toMap(),
                )
            } catch (e: ApiException) {
                _uiState.value = TariffsUiState.Error(message = e.message)
            }
        }
    }
}