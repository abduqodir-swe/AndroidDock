package dev.androiddock.core.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class MonthGrid(val weekdays: List<DayOfWeek>, val cells: List<LocalDate?>)

fun monthGrid(month: YearMonth, firstDay: DayOfWeek): MonthGrid {
    val offset = (month.atDay(1).dayOfWeek.value - firstDay.value + 7) % 7
    val count = ((offset + month.lengthOfMonth() + 6) / 7) * 7
    return MonthGrid(
        weekdays = (0..6).map { firstDay.plus(it.toLong()) },
        cells = (0 until count).map { index ->
            (index - offset + 1).takeIf { it in 1..month.lengthOfMonth() }?.let(month::atDay)
        },
    )
}

sealed interface Availability<out T> {
    data class Data<T>(val value: T) : Availability<T>
    data object NoPermission : Availability<Nothing>
    data object Unavailable : Availability<Nothing>
}

data class BatteryState(val percent: Int, val charging: Boolean)

fun batteryPercent(level: Int, scale: Int): Int? =
    if (scale <= 0 || level < 0 || level > scale) null else (level.toLong() * 100 / scale).toInt()
