package com.compostheap.diary.data.local

import android.content.Context
import com.compostheap.diary.domain.model.AppSettings
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.ThemeVariant
import com.compostheap.diary.domain.model.VolumeUnit

class SettingsPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun load(): AppSettings = AppSettings(
        volumeUnit = if (preferences.getString(KEY_VOLUME, "").orEmpty() == "GALLONS") {
            VolumeUnit.GALLONS
        } else {
            VolumeUnit.LITRES
        },
        tempUnit = if (preferences.getString(KEY_TEMP, "").orEmpty() == "FAHRENHEIT") {
            TempUnit.FAHRENHEIT
        } else {
            TempUnit.CELSIUS
        },
        theme = if (preferences.getString(KEY_THEME, "").orEmpty() == "HUMUS_DEEP") {
            ThemeVariant.HUMUS_DEEP
        } else {
            ThemeVariant.SOIL_DARK
        }
    )

    fun save(settings: AppSettings) {
        preferences.edit()
            .putString(KEY_VOLUME, settings.volumeUnit.name)
            .putString(KEY_TEMP, settings.tempUnit.name)
            .putString(KEY_THEME, settings.theme.name)
            .apply()
    }

    private companion object {
        const val STORE = "compost_diary_settings"
        const val KEY_VOLUME = "volume_unit"
        const val KEY_TEMP = "temp_unit"
        const val KEY_THEME = "theme_variant"
    }
}
