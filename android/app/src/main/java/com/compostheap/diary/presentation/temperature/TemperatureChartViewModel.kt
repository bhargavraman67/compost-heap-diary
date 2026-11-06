package com.compostheap.diary.presentation.temperature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.domain.model.TemperatureReading
import com.compostheap.diary.domain.repository.CompostRepository
import com.compostheap.diary.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class TemperatureChartViewModel(
    private val binId: String,
    private val repository: CompostRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TemperatureChartUiState())
    val uiState: StateFlow<TemperatureChartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.bins,
                repository.readings,
                settingsRepository.settings
            ) { bins, readings, settings ->
                val own = readings
                    .filter { it.binId == binId }
                    .sortedBy { it.timestampMs }
                TemperatureChartUiState(
                    binName = bins.firstOrNull { it.id == binId }?.name.orEmpty(),
                    readings = own,
                    peakCelsius = own.maxOfOrNull { it.celsius } ?: 0f,
                    averageCelsius = if (own.isEmpty()) {
                        0f
                    } else {
                        own.sumOf { it.celsius.toDouble() }.toFloat() / own.size
                    },
                    tempUnit = settings.tempUnit
                )
            }.collect { data ->
                _uiState.update { current -> data.copy(showDialog = current.showDialog) }
            }
        }
    }

    fun openDialog() {
        _uiState.update { it.copy(showDialog = true) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showDialog = false) }
    }

    fun addReading(celsius: Float) {
        repository.addReading(
            TemperatureReading(
                id = UUID.randomUUID().toString(),
                binId = binId,
                celsius = celsius.coerceIn(GameConfig.MIN_CELSIUS, GameConfig.MAX_CELSIUS),
                timestampMs = System.currentTimeMillis()
            )
        )
        _uiState.update { it.copy(showDialog = false) }
    }
}
