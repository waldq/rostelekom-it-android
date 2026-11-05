package dev.waldq.rostelekom.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.AuthApi
import dev.waldq.rostelekom.models.RegisterDto
import dev.waldq.rostelekom.validation.filterName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    phone: String,
    registrationToken: String,
    private val repository: AuthApi,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState(phone = phone, registrationToken = registrationToken))
    val uisState = _uiState.asStateFlow() // снаружи только изменяемое сотояние

    fun onFirstNameChange(name: String) {
        _uiState.update { it.copy(firstName = filterName(name), firstNameError = null) }
    }

    fun onLastNameChange(name: String) {
        _uiState.update { it.copy(lastName = filterName(name), lastNameError = null) }
    }

    fun onMiddleNameChange(name: String) {
        _uiState.update { it.copy(middleName = filterName(name), middleNameError = null) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isLoading || !state.isButtonEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(
                firstNameError = null,
                lastNameError = null,
                middleNameError = null,
                isLoading = true,
            ) }

            try {
                repository.registerAccount(RegisterDto(
                    registrationToken = state.registrationToken,
                    lastName = state.lastName.trim(),
                    firstName = state.firstName.trim(),
                    middleName = state.middleName.trim().ifBlank { null },
                    )
                )
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: ApiException) {
                // TODO
                _uiState.update { it.copy(
                    firstNameError = e.message,
                    lastNameError = e.message,
                    middleNameError = e.message
                ) }
            }
        }
    }

    fun onLoggedInHandled() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }
}