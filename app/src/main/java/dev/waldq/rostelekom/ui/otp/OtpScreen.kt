package dev.waldq.rostelekom.ui.otp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.waldq.rostelekom.R
import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.ui.components.formatPhoneForDisplay
import dev.waldq.rostelekom.ui.theme.ButtonContainerDisabledGray
import dev.waldq.rostelekom.ui.theme.ButtonTextDisabledGray
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme
import dev.waldq.rostelekom.validation.CODE_LENGTH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    uiState: OtpUiState,
    onBack: () -> Unit,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onResend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
        // верхняя область со стрелкой возврата назад
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Назад"
                    )
                }
            },
        ) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .padding(innerPadding)
            .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Введите код из SMS",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Код отправлен на +7 ${formatPhoneForDisplay(uiState.phone)}",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            // кнопка возврата обратно на страницу логину
            TextButton(
                onClick = onBack,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Изменить номер",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // TODO убрать после подключения SMS сервиса
            if (uiState.devCode != null) {
                Text(
                    text = "Код для тестового входа: ${uiState.devCode}"
                )
            }

            Spacer(Modifier.height(20.dp))

            // текстовое поле, поделённое на клетки
            BasicTextField(
                value = uiState.code,
                onValueChange = onCodeChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                decorationBox = { innerTextField ->
                    BoxedTextField(code = uiState.code, error = uiState.error, innerTextField = innerTextField)
                }
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            // обновляемый текст с таймером до повторного запроса кода
            if (uiState.retryAfterSeconds > 0) {
                Text(
                    text = buildAnnotatedString {
                        append("Отправить код повторно через ")
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )) { append(uiState.resendTimeText) }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                TextButton(
                    onClick = onResend
                ) {
                    Text("Отправить код ещё раз")
                }
            }


            Spacer(Modifier.weight(1f))

            // кнопка ввода. Активна, когда номер корректный. TODO
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = uiState.isButtonEnabled,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = ButtonContainerDisabledGray,
                    disabledContentColor = ButtonTextDisabledGray,
                ),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = LocalContentColor.current,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = "Войти")
                }
            }
        }
    }
}

/**
 * Функция, рисующая текстовое поле, разделённое на квадраты, по одному на символ, длины [CODE_LENGTH].
 */
@Composable
private fun BoxedTextField(code: String, error: String?, innerTextField: @Composable (() -> Unit)) {
    Box(Modifier.size(0.dp)) { innerTextField() }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(CODE_LENGTH) { i ->
            val isActive = i == code.length
            val borderColor = when {
                error != null -> MaterialTheme.colorScheme.error
                isActive -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }
            val borderWidth = if (error != null || isActive) 2.dp else 1.dp

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = code.getOrNull(i)?.toString() ?: "")
            }
        }
    }
}

@Composable
fun OtpRoute(
    phone: String,
    devCode: String?,
    resendAfterSeconds: Long,
    onBack: () -> Unit,
    onRegistrationRequired: (String) -> Unit,
    onLoggedIn: () -> Unit,
    viewModel: OtpViewModel = viewModel { OtpViewModel(
        phone = phone,
        devCode = devCode,
        retryAfterSeconds = resendAfterSeconds,
        repository = AppContainer.authRepository
    ) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoggedIn()
            viewModel.onLoggedInHandled()
        }
    }

    LaunchedEffect(uiState.registrationToken) {
        uiState.registrationToken?.let {
            onRegistrationRequired(it)
            viewModel.onRegistrationHandled()
        }
    }

    OtpScreen(
        uiState = uiState,
        onBack = onBack,
        onCodeChange = viewModel::onCodeChange,
        onSubmit = viewModel::onSubmit,
        onResend = viewModel::onResend
    )
}

@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp")
@Composable
private fun OtpScreenSuccessPreview() {
    RostelekomitandroidTheme {
        OtpScreen(
            uiState = OtpUiState(phone = "8005556767", devCode = "231950", retryAfterSeconds = 48),
            onBack = {},
            onCodeChange = {},
            onSubmit = {},
            onResend = {}
        )
    }
}

@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp")
@Composable
private fun OtpScreenErrorPreview() {
    RostelekomitandroidTheme {
        OtpScreen(
            uiState = OtpUiState(phone = "8005556767", devCode = "231950", error = "Неверный код", retryAfterSeconds = 48),
            onBack = {},
            onCodeChange = {},
            onSubmit = {},
            onResend = {}
        )
    }
}