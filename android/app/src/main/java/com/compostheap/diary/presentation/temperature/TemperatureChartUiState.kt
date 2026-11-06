package com.compostheap.diary.presentation.temperature

import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.TemperatureReading

data class TemperatureChartUiState(
    val binName: String = "",
    val readings: List<TemperatureReading> = emptyList(),
    val peakCelsius: Float = 0f,
    val averageCelsius: Float = 0f,
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val showDialog: Boolean = false
)
