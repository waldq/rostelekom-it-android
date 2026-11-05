package dev.waldq.rostelekom.ui.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.LineBreak
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

@Composable
fun RegistrationRoute(
    phone: String,
    registrationToken: String,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
    onLoggedIn: () -> Unit,
    viewModel: RegistrationViewModel = viewModel { RegistrationViewModel(
        phone = phone,
        registrationToken = registrationToken,
        repository = AppContainer.authRepository
    ) }
) {
    val uiState by viewModel.uisState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoggedIn()
            viewModel.onLoggedInHandled()
        }
    }

    RegistrationScreen(
        uiState = uiState,
        onBack = onBack,
        onSessionExpired = onSessionExpired,
        onFirstNameTextFieldChange = viewModel::onFirstNameChange,
        onLastNameTextFieldChange = viewModel::onLastNameChange,
        onMiddleNameTextFieldChange = viewModel::onMiddleNameChange,
        onSubmit = viewModel::onSubmit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    uiState: RegistrationUiState,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
    onFirstNameTextFieldChange: (String) -> Unit,
    onLastNameTextFieldChange: (String) -> Unit,
    onMiddleNameTextFieldChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
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
                text = "Создание аккаунта",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("Номер ")
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )) { append("+7 ${formatPhoneForDisplay(uiState.phone)} ") }
                    append("подтверждён. Заполните данные профиля")
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium.copy(lineBreak = LineBreak.Heading),
            )

            Spacer(Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NameOutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = onLastNameTextFieldChange,
                    isEnabled = !uiState.isLoading,
                    label = { Text("Фамилия") },
                    placeholder = { Text("Чебупелин") },
                    error = uiState.lastNameError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                NameOutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = onFirstNameTextFieldChange,
                    isEnabled = !uiState.isLoading,
                    label = { Text("Имя") },
                    placeholder = { Text("Жека") },
                    error = uiState.firstNameError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                NameOutlinedTextField(
                    value = uiState.middleName,
                    onValueChange = onMiddleNameTextFieldChange,
                    isEnabled = !uiState.isLoading,
                    label = { Text("Отчество") },
                    placeholder = { Text("Толянович") },
                    error = uiState.middleNameError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Text(
                text = "Необязательно",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top  = 4.dp, end = 16.dp, bottom = 0.dp),
            )

            Spacer(Modifier.weight(1f))

            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            // кнопка ввода. Активна, когда поля корректны. TODO
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
                    Text(text = "Зарегистрироваться")
                }
            }
        }

        if (uiState.isSessionExpired) {
            AlertDialog(
                onDismissRequest = onSessionExpired,
                confirmButton = {
                    TextButton(onClick = onSessionExpired) { Text("Войти заново") }
                },
                title = { Text("Время вышло") },
                text = { Text("Сессия регистрации истекла. Подтвердите номер ещё раз — это займёт минуту.") },
            )
        }
    }
}

@Composable
private fun NameOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean,
    label: @Composable () -> Unit,
    placeholder: @Composable () -> Unit,
    error: String?,
    hint: String? = null,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
) {
    val text = error ?: hint

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = isEnabled,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        supportingText = if (text != null) { { Text(text) } } else { null },
        isError = error != null,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(10.dp),
    )
}

@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp")
@Composable
private fun RegistrationScreenPreview() {
    RostelekomitandroidTheme {
        RegistrationScreen(
            uiState = RegistrationUiState(
                phone = "9991234567",
                registrationToken = "registrationToken",
            ),
            onBack = {},
            onSessionExpired = {},
            onSubmit = {},
            onFirstNameTextFieldChange = {},
            onLastNameTextFieldChange = {},
            onMiddleNameTextFieldChange = {},
        )
    }
}