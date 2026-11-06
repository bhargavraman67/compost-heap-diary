package com.compostheap.diary.presentation.settings

import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.ThemeVariant
import com.compostheap.diary.domain.model.VolumeUnit

data class SettingsUiState(
    val volumeUnit: VolumeUnit = VolumeUnit.LITRES,
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val theme: ThemeVariant = ThemeVariant.SOIL_DARK,
    val exportText: String = "",
    val showExport: Boolean = false,
    val showImport: Boolean = false,
    val importFailed: Boolean = false
)
