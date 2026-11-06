package com.compostheap.diary.core.util

import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.VolumeUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object Formats {

    private val dayMonthFormat = SimpleDateFormat("d MMM", Locale.US)
    private val dayMonthYearFormat = SimpleDateFormat("d MMM yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    fun dayMonth(timestampMs: Long): String =
        dayMonthFormat.format(Date(timestampMs)).uppercase(Locale.US)

    fun dayMonthYear(timestampMs: Long): String =
        dayMonthYearFormat.format(Date(timestampMs)).uppercase(Locale.US)

    fun time(timestampMs: Long): String = timeFormat.format(Date(timestampMs))

    fun volumeValue(litres: Float, unit: VolumeUnit): String = when (unit) {
        VolumeUnit.LITRES -> litres.roundToInt().toString()
        VolumeUnit.GALLONS -> String.format(
            Locale.US,
            "%.1f",
            litres / GameConfig.LITRES_PER_GALLON
        )
    }

    fun temperatureValue(celsius: Float, unit: TempUnit): Int = when (unit) {
        TempUnit.CELSIUS -> celsius.roundToInt()
        TempUnit.FAHRENHEIT -> (celsius * 9f / 5f + 32f).roundToInt()
    }

    fun percent(ratio: Float): Int = (ratio.coerceIn(0f, 1f) * 100f).roundToInt()
}
