package com.compostheap.diary.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.domain.repository.CompostRepository
import com.compostheap.diary.domain.repository.SettingsRepository
import com.compostheap.diary.domain.usecase.BuildTimelineUseCase
import com.compostheap.diary.domain.usecase.SummariseBinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BinTimelineViewModel(
    private val binId: String,
    private val repository: CompostRepository,
    private val settingsRepository: SettingsRepository,
    private val summariseBin: SummariseBinUseCase,
    private val buildTimeline: BuildTimelineUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BinTimelineUiState())
    val uiState: StateFlow<BinTimelineUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.bins,
                repository.entries,
                repository.readings,
                repository.materials,
                settingsRepository.settings
            ) { bins, entries, readings, materials, settings ->
                val bin = bins.firstOrNull { it.id == binId }
                val summary = bin?.let {
                    summariseBin(it, entries, materials, System.currentTimeMillis())
                }
                BinTimelineUiState(
                    binName = bin?.name.orEmpty(),
                    startedAtMs = bin?.startedAtMs ?: 0L,
                    dayCount = summary?.dayCount ?: 0,
                    daysSinceTurn = summary?.daysSinceTurn,
                    resting = bin?.resting ?: false,
                    greenRatio = summary?.balance?.greenRatio ?: 0.5f,
                    items = buildTimeline(binId, entries, readings, materials),
                    volumeUnit = settings.volumeUnit,
                    tempUnit = settings.tempUnit
                )
            }.collect { data ->
                _uiState.update { current ->
                    data.copy(showConfirmHarvest = current.showConfirmHarvest)
                }
            }
        }
    }

    fun toggleResting() {
        repository.setResting(binId, !_uiState.value.resting)
    }

    fun askHarvest() {
        _uiState.update { it.copy(showConfirmHarvest = true) }
    }

    fun dismissHarvest() {
        _uiState.update { it.copy(showConfirmHarvest = false) }
    }
}
