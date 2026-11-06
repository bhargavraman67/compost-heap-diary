package com.compostheap.diary.domain.repository

import com.compostheap.diary.domain.model.AppSettings
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.ThemeVariant
import com.compostheap.diary.domain.model.VolumeUnit
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settings: StateFlow<AppSettings>

    fun setVolumeUnit(unit: VolumeUnit)

    fun setTempUnit(unit: TempUnit)

    fun setTheme(theme: ThemeVariant)
}
