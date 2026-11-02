package dev.waldq.rostelekom.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Класс для приведения вводимого номера телефона к формату (xxx) xxx-xx-xx
 */
object PhoneVisualTransformation : VisualTransformation {
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