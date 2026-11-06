package com.compostheap.diary.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.ThemeVariant
import com.compostheap.diary.domain.model.VolumeUnit
import com.compostheap.diary.domain.repository.SettingsRepository
import com.compostheap.diary.domain.usecase.ExportDiaryUseCase
import com.compostheap.diary.domain.usecase.ImportDiaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val exportDiary: ExportDiaryUseCase,
    private val importDiary: ImportDiaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update {
                    it.copy(
                        volumeUnit = settings.volumeUnit,
                        tempUnit = settings.tempUnit,
                        theme = settings.theme
                    )
                }
            }
        }
    }

    fun setVolumeUnit(unit: VolumeUnit) {
        settingsRepository.setVolumeUnit(unit)
    }

    fun setTempUnit(unit: TempUnit) {
        settingsRepository.setTempUnit(unit)
    }

    fun setTheme(theme: ThemeVariant) {
        settingsRepository.setTheme(theme)
    }

    fun openExport() {
        _uiState.update { it.copy(exportText = exportDiary(), showExport = true) }
    }

    fun dismissExport() {
        _uiState.update { it.copy(showExport = false) }
    }

    fun openImport() {
        _uiState.update { it.copy(showImport = true, importFailed = false) }
    }

    fun dismissImport() {
        _uiState.update { it.copy(showImport = false, importFailed = false) }
    }

    fun confirmImport(raw: String) {
        val success = importDiary(raw)
        _uiState.update {
            if (success) {
                it.copy(showImport = false, importFailed = false)
            } else {
                it.copy(importFailed = true)
            }
        }
    }
}
