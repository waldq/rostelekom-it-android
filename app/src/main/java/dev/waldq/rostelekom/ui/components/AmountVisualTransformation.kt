package dev.waldq.rostelekom.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val GROUP_SEPARATOR = '\u00A0' // неразрывный пробел

/**
 * VisualTransformation для поля суммы: `1250.5` в состоянии показывается как `1 250,5`.
 *
 * В состоянии остаётся «сырая» строка (цифры и один разделитель, `.` или `,`), форматируется только то,
 * что видно в поле. Поэтому в ViewModel парсить нужно сырую строку: `raw.replace(',', '.').toDoubleOrNull()`.
 *
 * Разряды разделяются пробелом, дробная часть всегда через запятую. Курсор не прыгает: позиции
 * в сырой и показанной строке пересчитываются через [OffsetMapping].
 *
 * Использование: `OutlinedTextField(visualTransformation = AmountVisualTransformation, ...)`
 */
object AmountVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val separatorIndex = raw.indexOfFirst { it == '.' || it == ',' }
        val integerLength = if (separatorIndex == -1) raw.length else separatorIndex

        // индексы в сырой строке, перед которыми вставляется пробел: 1 234 567 -> перед '2' и перед '5'
        val spaceBefore = (1 until integerLength).filter { (integerLength - it) % 3 == 0 }

        val shown = buildString {
            raw.forEachIndexed { index, char ->
                if (index in spaceBefore) append(GROUP_SEPARATOR)
                append(if (char == '.') ',' else char)
            }
        }

        val mapping = object : OffsetMapping {
            // сколько пробелов окажется левее курсора
            override fun originalToTransformed(offset: Int): Int =
                offset + spaceBefore.count { it <= offset }

            // k-я вставка стоит на позиции spaceBefore[k] + k в показанной строке
            override fun transformedToOriginal(offset: Int): Int =
                (offset - spaceBefore.withIndex().count { (k, index) -> index + k < offset })
                    .coerceIn(0, raw.length)
        }

        return TransformedText(AnnotatedString(shown), mapping)
    }
}