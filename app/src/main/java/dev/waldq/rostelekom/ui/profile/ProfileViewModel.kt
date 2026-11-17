package dev.waldq.rostelekom.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.BillingApi
import dev.waldq.rostelekom.data.repository.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserApi,
    private val billingRepository: BillingApi, // мб пригодится TODO
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val user = userRepository.getProfile()
                val account = userRepository.getMyAccount()
                _uiState.value = ProfileUiState.Content(
                    phone = user.phone,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    middleName = user.middleName,
                    accountNumber = "№ ${account.number.chunked(4).joinToString(" ")}",
                )
            } catch (e: ApiException) {
                _uiState.value = ProfileUiState.Error(e.message)
            }
        }
    }
}