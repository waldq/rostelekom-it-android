package dev.waldq.rostelekom.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.waldq.rostelekom.data.remote.AuthApi
import dev.waldq.rostelekom.data.repository.ApiException
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.models.OtpVerifyDto
import dev.waldq.rostelekom.validation.filterCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class OtpViewModel(
    phone: String,
    devCode: String?,
    retryAfterSeconds: Long,
    private val repository: AuthApi
): ViewModel() {
    private val _uiState = MutableStateFlow(OtpUiState(phone = phone, devCode = devCode))
    val uiState = _uiState.asStateFlow() // снаружи только неизменяемое состояние

    private var retryTimer: Job? = null // корутина таймера

    // запускаем таймер при открытии экрана, чтобы нельзя было сразу же запросить следующий код,
    // а то у Потапа на бэке жёсткие ограничения вроде на коды
    init {
        startRetryTimer(retryAfterSeconds)
    }

    /**
     * Функция для обновления кода.
     *
     * Будет передаваться в поле ввода кода.
     *
     * @param code новый текст в поле кода.
     */
    fun onCodeChange(code: String) {
        _uiState.update { it.copy(code = filterCode(code), error = null) }
    }

    /**
     * Функция, которая вызывается при нажатии кнопки отправления кода подтверждения.
     *
     * Отправляет код, получает токены и перенаправляет юзера на главную.
     */
    fun onSubmit() {
        val state = _uiState.value
        if (state.isLoading || !state.isButtonEnabled) return   // защита от двойного касания

        viewModelScope.launch {
            _uiState.update { it.copy(error = null, isLoading = true) }
            try {
                // запрашиваем токены с сервера
                val tokens = repository.verifyCode(OtpVerifyDto(phone = "+7${state.phone}", code = state.code))

                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                // TODO записать токены и перейти на главную страницу скорее всего
            } catch (e: ApiException) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    /**
     * Функция для повторной отправки кода подтверждения.
     */
    fun onResend() {
        val state = _uiState.value
        if (state.isLoading || state.retryAfterSeconds > 0) return   // защита от двойного касания

        // запрос на получения OTP кода в корутине
        viewModelScope.launch {
            _uiState.update { it.copy(code = "", error = null, isLoading = true) }
            try {
                // пробуем отправить запрос с на получение кода
                val challenge = repository.requestCode(OtpRequestDto("+7${state.phone}"))

                _uiState.update { it.copy(devCode = challenge.code, isLoading = false) }
                startRetryTimer(challenge.resendAfterSeconds)
            } catch (e: ApiException) {
                _uiState.update { it.copy(isLoading = false) }

                if (e.retryAfterSeconds > 0) {
                    startRetryTimer(e.retryAfterSeconds)
                } else {
                    _uiState.update { it.copy(error = e.message) }
                }
            }
        }
    }

    fun onLoggedInHandled() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    // функция, которая запускаяет корутину таймера
    private fun startRetryTimer(seconds: Long) {
        retryTimer?.cancel()
        retryTimer = viewModelScope.launch {
            for (left in seconds downTo 1 ) {
                _uiState.update { it.copy(retryAfterSeconds = left) }
                delay(1000.milliseconds)
            }
            _uiState.update { it.copy(retryAfterSeconds = 0) }
        }
    }
}