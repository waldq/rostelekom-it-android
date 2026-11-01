package dev.waldq.rostelekom.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import dev.waldq.rostelekom.ui.theme.*
import dev.waldq.rostelekom.validation.*

/**
 * Входная функция экрана логина.
 *
 * Её нужно будет оборачивать в нашу тему, как, в целом, и остальные экраны.
 *
 * Ничего передавать сюда не нужно.
 *
 * Визуально экран делится на верхнюю часть (шапку [LoginHeader]) и нижнюю (форму [LoginForm]).
 */
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var phone by rememberSaveable { mutableStateOf("") }

    Box(modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(Modifier.fillMaxSize()
        ) {
            LoginHeader(Modifier.fillMaxWidth())
            LoginForm(
                phone = phone,
                onPhoneChange = { phone = filterPhone(it) },
                modifier = Modifier.weight(1f),
            )
        }
    }
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
 * Класс для приведения вводимого номера телефона к формату (xxx) xxx-xx-xx
 */
class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(10)

        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                when (i) {
                    0 -> append("(")
                    3 -> append(") ")
                    6, 8 -> append("-")
                }
                append(c)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val o = offset.coerceIn(0, digits.length)
                val extra = when {
                    o == 0 -> 0
                    o <= 3 -> 1
                    o <= 6 -> 3
                    o <= 8 -> 4
                    else -> 5
                }
                return o + extra
            }

            override fun transformedToOriginal(offset: Int): Int {
                val o = when {
                    offset <= 1 -> 0
                    offset <= 4 -> offset - 1
                    offset <= 6 -> 3
                    offset <= 9 -> offset - 3
                    offset == 10 -> 6
                    offset <= 12 -> offset - 4
                    offset == 13 -> 8
                    else -> offset - 5
                }
                return o.coerceIn(0, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
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
    modifier: Modifier = Modifier,
) {
    // TODO доделать форматирование при отображении
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text("Номер телефона") },
        placeholder = { Text("(999) 123-45-67") },
        prefix = { Text("+7 ") }, // TODO подумать, делать префикс того же цвета, что и сам номер, или нет
        visualTransformation = PhoneVisualTransformation(),
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
            )
            Spacer(Modifier.weight(1f))
            // кнопка ввода. Активна, когда номер корректный. TODO
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = false,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = ButtonContainerDisabledGray,
                    disabledContentColor = ButtonTextDisabledGray,
                ),
            ) {
                Text(
                    text = "Получить код",
                )
            }
        }
    }
}

// @PreviewScreenSizes // показывает несколько экранов
@Preview(showSystemUi = true, device = "spec:width=360dp,height=780dp") // работает и ладно
@Composable
private fun LoginScreenPreview() {
    RostelekomitandroidTheme {
        LoginScreen()
    }
}
