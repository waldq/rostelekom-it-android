package dev.waldq.rostelekom.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
val futureSelectableDates = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        // Начало сегодняшнего дня в UTC (чтобы «сегодня» тоже было доступно)
        val todayStartMillis = LocalDate.now()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        return utcTimeMillis >= todayStartMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}