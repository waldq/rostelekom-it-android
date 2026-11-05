package dev.waldq.rostelekom.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.waldq.rostelekom.di.AppContainer
import dev.waldq.rostelekom.models.OtpChallengeDto
import dev.waldq.rostelekom.ui.components.PhoneVisualTransformation
import dev.waldq.rostelekom.ui.theme.ButtonContainerDisabledGray
import dev.waldq.rostelekom.ui.theme.ButtonTextDisabledGray
import dev.waldq.rostelekom.ui.theme.RostelekomitandroidTheme

/**
 * Визуально экран делится на верхнюю часть (шапку [LoginHeader]) и нижнюю (форму [LoginForm]).
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onPhoneChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(Modifier.fillMaxSize()
        ) {
            LoginHeader(Modifier.fillMaxWidth())
            LoginForm( // экран и форма сами ничего не хранят и не обновляют
                phone = uiState.phone,
                onPhoneChange = onPhoneChange,
                error = uiState.error,
                buttonText = uiState.buttonText,
                isButtonEnabled = uiState.isButtonEnabled,
                onSubmit = onSubmit,
                isLoading = uiState.isLoading,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Функция, отвечающая за связь экрана с ViewModel.
 */
@Composable
fun LoginRoute(
    onCodeSent: (phone: String, challenge: OtpChallengeDto) -> Unit,  // это вызов из навигации, тут переходим на экран ввода кода
    viewModel: LoginViewModel = viewModel { LoginViewModel(AppContainer.authRepository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // выполняется, когда изменилось состояние challenge (пришёл ответ в частности)
    LaunchedEffect(uiState.challenge) {
        val challenge = uiState.challenge ?: return@LaunchedEffect
        onCodeSent(uiState.phone, challenge)
        viewModel.onCodeSentHandled()
    }

    LoginScreen(
        uiState = uiState,
        onPhoneChange = viewModel::onPhoneChange,
        onSubmit = viewModel::onSubmit,
    )
}

/**
 * Круги на фиолетовом фоне, маленький оранжевый поверх большого почти прозрачного белого.
 */
@Composable
private fun BackgroundCircles(modifier: Modifier = Modifier) {
    Box(modifier) {
        Box(Modifier
            .align(Alignment.BottomEnd)
            .offset(x = 80.dp, y = 126.dp)
            .size(220.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
        )
        Box(Modifier
            .align(Alignment.BottomEnd)
            .offset(x = (-40).dp, y = 96.dp)
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f))
        )
    }
}

/**
 * Шапка.
 *
 * Состоит из колонки с логотипом, заголовком и описанием.
 */
@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    Box(modifier
    ) {
        BackgroundCircles(Modifier.matchParentSize())
        Column(Modifier
            .statusBarsPadding()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            // затычка логотипа с захардкоженными параметрами. цвета при этом передаются через тему, а не напрямую.
            Box(Modifier
                .size(width = 132.dp, height = 36.dp)
                .border(
                    width = 1.dp,
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                ),
                contentAlignment = Alignment.Center
            ) { // TODO здесь будет лого
                Text(
                    text = "логотип",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(28.dp)) // разделитель, чтобы не пихать везде padding
            // заголовок
            Text(
                text = "Управляйте услугами связи в одном месте",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(Modifier.height(8.dp))
            // описание
            Text(
                text = "Проверяйте баланс, пополняйте счёт и смотрите историю",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f),
                // обрывает строку +- по середине, чтобы не хардкодить
                // максимальную ширину, потому что на широких экранах писалось в 2 строки
                style = MaterialTheme.typography.bodyLarge.copy(lineBreak = LineBreak.Heading),
            )
        }
    }
}

/**
 * Поле ввода номера.
 *
 * Вынесено, потому что будет больше параметров и может потребоваться переиспользовать.
 * А ещё нужно передавать сюда значение и лямбду [LoginScreen].
 * Это же значение будет влиять на кнопку, поэтому храним его выше, чем поле.
 */
@Composable
private fun PhoneOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text("Номер телефона") },
        placeholder = { Text("(999) 123-45-67") },
        prefix = { Text("+7 ") }, // TODO подумать, делать префикс того же цвета, что и сам номер, или нет
        supportingText = if (error != null) { { Text(error) } } else { null },
        isError = error != null,
        visualTransformation = PhoneVisualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        shape = RoundedCornerShape(10.dp),
    )
}

/**
 * Форма логина.
 */
@Composable
private fun LoginForm(
    phone: String,
    onPhoneChange: (String) -> Unit,
    error: String?,
    isButtonEnabled: Boolean,
    buttonText: String,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface( // белый блок с закруглёнными углами
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(20.dp)) {

            Spacer(Modifier.height(8.dp))

            // заголовок
            Text(
                text = "Вход в личный кабинет",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(Modifier.height(6.dp))

            // ну ты понял
            Text(
                text = "Введите номер телефона, привязанный к лицевому счёту",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(24.dp))

            // поле ввода
            PhoneOutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                modifier = Modifier.fillMaxWidth(),
                error = error,
            )

            Spacer(Modifier.weight(1f))

            // кнопка ввода. Активна, когда номер корректный.
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = ButtonContainerDisabledGray,
                    disabledContentColor = ButtonTextDisabledGray,
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = LocalContentColor.current,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = buttonText,
                    )
                }
            }
        }
    }
}

// @PreviewScreenSizes // показывает несколько экранов
@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp") // работает и ладно
@Composable
private fun LoginScreenPreview() {
    RostelekomitandroidTheme {
        LoginScreen(
            uiState = LoginUiState(),
            onPhoneChange = {},
            onSubmit = {}
        )
    }
}

// превью при пустом поле номера
@Preview(showBackground = true, widthDp = 360, name = "Пусто")
@Composable
private fun LoginFormEmptyPreview() {
    RostelekomitandroidTheme {
        LoginForm(
            phone = "",
            onPhoneChange = {},
            error = null,
            isButtonEnabled = false,
            buttonText = "Получить код",
            onSubmit = {},
            isLoading = false,
            modifier = Modifier.height(500.dp),
        )
    }
}

// превью с введённым номером
@Preview(showBackground = true, widthDp = 360, name = "Номер введён")
@Composable
private fun LoginFormNumberFilled() {
    RostelekomitandroidTheme {
        LoginForm(
            phone = "9991234567",
            onPhoneChange = {},
            error = null,
            isButtonEnabled = true,
            buttonText = "Получить код",
            onSubmit = {},
            isLoading = false,
            modifier = Modifier.height(500.dp),
        )
    }
}

// превью в состоянии загрузки (ожидания ответа сервера)
@Preview(showBackground = true, widthDp = 360, name = "Загрузка")
@Composable
private fun LoginFormLoadingPreview() {
    RostelekomitandroidTheme {
        LoginForm(
            phone = "9991234567",
            onPhoneChange = {},
            error = null,
            isButtonEnabled = true,
            buttonText = "Получить код",
            onSubmit = {},
            isLoading = true,
            modifier = Modifier.height(500.dp),
        )
    }
}

// превью в состоянии ошибки
@Preview(showBackground = true, widthDp = 360, name = "Ошибка")
@Composable
private fun LoginFormErrorPreview() {
    RostelekomitandroidTheme {
        LoginForm(
            phone = "9991234567",
            onPhoneChange = {},
            error = "Сервис пользователей временно недоступен, попробуйте позже.",
            isButtonEnabled = true,
            buttonText = "Получить код",
            onSubmit = {},
            isLoading = false,
            modifier = Modifier.height(500.dp),
        )
    }
}

// превью с таймером на кнопке
@Preview(showBackground = true, widthDp = 360, name = "Таймер")
@Composable
private fun LoginFormTimerPreview() {
    RostelekomitandroidTheme {
        LoginForm(
            phone = "9991234567",
            onPhoneChange = {},
            error = null,
            isButtonEnabled = false,
            buttonText = "Повторить через 0:42",
            onSubmit = {},
            isLoading = false,
            modifier = Modifier.height(500.dp),
        )
    }
}

