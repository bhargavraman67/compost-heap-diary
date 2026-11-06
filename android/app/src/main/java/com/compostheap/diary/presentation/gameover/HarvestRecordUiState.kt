package com.compostheap.diary.presentation.gameover

import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.VolumeUnit

data class HarvestRecordUiState(
    val binName: String = "",
    val dayCount: Int = 0,
    val yieldLitres: Float = 0f,
    val peakCelsius: Float? = null,
    val greenRatio: Float = 0.5f,
    val note: String = "",
    val volumeUnit: VolumeUnit = VolumeUnit.LITRES,
    val tempUnit: TempUnit = TempUnit.CELSIUS
)
