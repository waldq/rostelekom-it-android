package dev.waldq.rostelekom.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.data.repository.AuthRepository
import dev.waldq.rostelekom.validation.filterPhone
import dev.waldq.rostelekom.models.OtpChallengeDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LoginViewModel(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow() // снаружи только неизменяемое состояние

    private var retryTimer: Job? = null // корутина таймера


    /**
     * Функция для обновления номера телефона.
     *
     * Будет передаваться в поле ввода номера.
     *
     * @param phone новый текст в поле номера телефона.
     */
    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = filterPhone(phone), error = null) }
    }

    /**
     * Функция для сброса [OtpChallengeDto] при отправке кода.
     */
    fun onCodeSentHandled() {
        _uiState.update { it.copy(challenge = null) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isLoading || !state.isButtonEnabled) return   // защита от двойного нажатия

        // запрос на получение OTP кода в корутине
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // пробуем отправить запрос с на получение кода
                val challenge = repository.requestCode("+7${state.phone}")

                // обновляем состояние, когда код пришёл
                _uiState.update { it.copy(isLoading = false, challenge = challenge) }

            } catch (e: ApiException) {
                // анлак, ошибка, выставляем ошибку в состоянии
                _uiState.update { it.copy(isLoading = false, error = e.message) }

                // если 429 (слишком много запросов) и у нас есть время - выставляем его в состоянии
                if (e.retryAfterSeconds > 0) {
                    startRetryTimer(e.retryAfterSeconds)
                } else {
                    _uiState.update { it.copy(error = e.message) }
                }
            }
        }
    }

    // функция, которая запускаяет корутину таймера
    private fun startRetryTimer(seconds: Long) {
        retryTimer?.cancel()
        retryTimer = viewModelScope.launch {
            for (left in seconds downTo 1 ) {
                _uiState.update { it.copy(retryAfterSeconds = left) }
                delay(1000.milliseconds)
            }
            _uiState.update { it.copy(retryAfterSeconds = 0, error = null) }
        }
    }
}
