package com.compostheap.diary.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.domain.repository.CompostRepository
import com.compostheap.diary.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PastBatchesViewModel(
    private val repository: CompostRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PastBatchesUiState())
    val uiState: StateFlow<PastBatchesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.batches, settingsRepository.settings) { batches, settings ->
                val durations = batches.map {
                    ((it.harvestedAtMs - it.startedAtMs) / GameConfig.DAY_MS).toInt()
                }
                PastBatchesUiState(
                    batches = batches,
                    averageDays = if (durations.isEmpty()) 0 else durations.sum() / durations.size,
                    totalYieldLitres = batches.sumOf { it.yieldLitres.toDouble() }.toFloat(),
                    volumeUnit = settings.volumeUnit,
                    tempUnit = settings.tempUnit
                )
            }.collect { data ->
                _uiState.update { current ->
                    data.copy(selectedBatchId = current.selectedBatchId)
                }
            }
        }
    }

    fun select(batchId: String) {
        _uiState.update { it.copy(selectedBatchId = batchId) }
    }

    fun dismissDetail() {
        _uiState.update { it.copy(selectedBatchId = null) }
    }
}
