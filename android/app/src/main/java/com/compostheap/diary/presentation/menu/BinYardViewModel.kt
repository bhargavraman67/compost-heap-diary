package com.compostheap.diary.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.domain.model.BalanceVerdict
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.repository.CompostRepository
import com.compostheap.diary.domain.repository.SettingsRepository
import com.compostheap.diary.domain.usecase.SummariseBinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BinYardViewModel(
    private val repository: CompostRepository,
    private val settingsRepository: SettingsRepository,
    private val summariseBin: SummariseBinUseCase
) : ViewModel() {

    private val dialogOpen = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(BinYardUiState())
    val uiState: StateFlow<BinYardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.bins,
                repository.entries,
                repository.materials,
                settingsRepository.settings,
                dialogOpen
            ) { bins, entries, materials, _, showAddBin ->
                val now = System.currentTimeMillis()
                val summaries = bins.map { summariseBin(it, entries, materials, now) }
                val green = summaries.sumOf { it.balance.greenVolume.toDouble() }.toFloat()
                val brown = summaries.sumOf { it.balance.brownVolume.toDouble() }.toFloat()
                val total = green + brown
                val ratio = if (total <= 0f) 0.5f else green / total
                val verdict = when {
                    total <= 0f -> BalanceVerdict.BALANCED
                    ratio > GameConfig.WET_THRESHOLD -> BalanceVerdict.TOO_WET
                    ratio < GameConfig.DRY_THRESHOLD -> BalanceVerdict.TOO_DRY
                    else -> BalanceVerdict.BALANCED
                }
                val monthAgo = now - 30L * GameConfig.DAY_MS
                BinYardUiState(
                    summaries = summaries,
                    greenRatio = ratio,
                    verdict = verdict,
                    activeBins = bins.count { !it.resting },
                    turnsThisMonth = entries.count {
                        it.entryType == EntryType.TURN && it.timestampMs >= monthAgo
                    },
                    showAddBin = showAddBin,
                    loaded = true
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun openAddBin() {
        dialogOpen.value = true
    }

    fun dismissAddBin() {
        dialogOpen.value = false
    }

    fun confirmAddBin(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        repository.addBin(trimmed)
        dialogOpen.value = false
    }
}
