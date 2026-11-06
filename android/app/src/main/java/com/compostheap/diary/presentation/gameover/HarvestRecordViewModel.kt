package com.compostheap.diary.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.domain.repository.CompostRepository
import com.compostheap.diary.domain.repository.SettingsRepository
import com.compostheap.diary.domain.usecase.HarvestBatchUseCase
import com.compostheap.diary.domain.usecase.SummariseBinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private data class ManualEdits(
    val note: String = "",
    val yieldOverride: Float? = null,
    val forStartedAtMs: Long = -1L
)

class HarvestRecordViewModel(
    private val binId: String,
    private val repository: CompostRepository,
    private val settingsRepository: SettingsRepository,
    private val summariseBin: SummariseBinUseCase,
    private val harvestBatch: HarvestBatchUseCase
) : ViewModel() {

    private val manual = MutableStateFlow(ManualEdits())

    private var committedStartedAtMs: Long = -1L

    private val _uiState = MutableStateFlow(HarvestRecordUiState())
    val uiState: StateFlow<HarvestRecordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.bins,
                repository.entries,
                repository.readings,
                settingsRepository.settings,
                manual
            ) { bins, entries, readings, settings, edits ->
                val bin = bins.firstOrNull { it.id == binId }
                val summary = bin?.let {
                    summariseBin(it, entries, repository.materials.value, System.currentTimeMillis())
                }
                val fresh = bin != null && edits.forStartedAtMs == bin.startedAtMs
                val peak = readings
                    .filter { it.binId == binId }
                    .maxOfOrNull { it.celsius }
                HarvestRecordUiState(
                    binName = bin?.name.orEmpty(),
                    dayCount = summary?.dayCount ?: 0,
                    yieldLitres = if (fresh && edits.yieldOverride != null) {
                        edits.yieldOverride
                    } else {
                        harvestBatch.estimateYield(binId)
                    },
                    peakCelsius = peak,
                    greenRatio = summary?.balance?.greenRatio ?: 0.5f,
                    note = if (fresh) edits.note else "",
                    volumeUnit = settings.volumeUnit,
                    tempUnit = settings.tempUnit
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun updateNote(text: String) {
        editFor { current, startedAt -> current.copy(note = text, forStartedAtMs = startedAt) }
    }

    fun increaseYield() {
        val next = (_uiState.value.yieldLitres + 1f).coerceAtMost(MAX_YIELD)
        editFor { current, startedAt ->
            current.copy(yieldOverride = next, forStartedAtMs = startedAt)
        }
    }

    fun decreaseYield() {
        val next = (_uiState.value.yieldLitres - 1f).coerceAtLeast(MIN_YIELD)
        editFor { current, startedAt ->
            current.copy(yieldOverride = next, forStartedAtMs = startedAt)
        }
    }

    fun commit() {
        val bin = repository.bins.value.firstOrNull { it.id == binId } ?: return
        if (committedStartedAtMs == bin.startedAtMs) return
        committedStartedAtMs = bin.startedAtMs
        harvestBatch(
            id = UUID.randomUUID().toString(),
            binId = binId,
            yieldLitres = _uiState.value.yieldLitres,
            note = _uiState.value.note.trim(),
            nowMs = System.currentTimeMillis()
        )
        manual.value = ManualEdits()
    }

    private fun editFor(transform: (ManualEdits, Long) -> ManualEdits) {
        val startedAt = repository.bins.value
            .firstOrNull { it.id == binId }
            ?.startedAtMs
            ?: return
        manual.update { current ->
            val base = if (current.forStartedAtMs == startedAt) current else ManualEdits()
            transform(base, startedAt)
        }
    }

    private companion object {
        const val MAX_YIELD = 400f
        const val MIN_YIELD = 1f
    }
}
