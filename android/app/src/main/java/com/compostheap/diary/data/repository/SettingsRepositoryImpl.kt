package com.compostheap.diary.data.repository

import com.compostheap.diary.data.local.SettingsPreferences
import com.compostheap.diary.domain.model.AppSettings
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.ThemeVariant
import com.compostheap.diary.domain.model.VolumeUnit
import com.compostheap.diary.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val preferences: SettingsPreferences
) : SettingsRepository {

    private val _settings = MutableStateFlow(preferences.load())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun setVolumeUnit(unit: VolumeUnit) {
        update(_settings.value.copy(volumeUnit = unit))
    }

    override fun setTempUnit(unit: TempUnit) {
        update(_settings.value.copy(tempUnit = unit))
    }

    override fun setTheme(theme: ThemeVariant) {
        update(_settings.value.copy(theme = theme))
    }

    private fun update(settings: AppSettings) {
        _settings.value = settings
        preferences.save(settings)
    }
}
