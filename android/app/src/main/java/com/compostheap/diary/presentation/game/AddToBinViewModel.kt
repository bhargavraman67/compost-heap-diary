package com.compostheap.diary.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.domain.model.TemperatureReading
import com.compostheap.diary.domain.repository.CompostRepository
import com.compostheap.diary.domain.repository.SettingsRepository
import com.compostheap.diary.domain.usecase.ComputeBalanceUseCase
import com.compostheap.diary.domain.usecase.LogEntryUseCase
import com.compostheap.diary.domain.usecase.SummariseBinUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private data class LocalState(
    val selectedType: MaterialType = MaterialType.GREEN,
    val selectedMaterialId: String? = null,
    val amount: Int = GameConfig.DEFAULT_AMOUNT,
    val banner: BannerKind? = null,
    val saving: Boolean = false,
    val turnTick: Int = 0,
    val waterTick: Int = 0,
    val showTemperatureDialog: Boolean = false
)

class AddToBinViewModel(
    private val binId: String,
    private val repository: CompostRepository,
    private val settingsRepository: SettingsRepository,
    private val summariseBin: SummariseBinUseCase,
    private val computeBalance: ComputeBalanceUseCase,
    private val logEntry: LogEntryUseCase
) : ViewModel() {

    private val local = MutableStateFlow(LocalState())

    private val _uiState = MutableStateFlow(AddToBinUiState())
    val uiState: StateFlow<AddToBinUiState> = _uiState.asStateFlow()

    private var bannerJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                repository.bins,
                repository.entries,
                repository.materials,
                settingsRepository.settings,
                local
            ) { bins, entries, materials, settings, localState ->
                val bin = bins.firstOrNull { it.id == binId }
                val summary = bin?.let {
                    summariseBin(it, entries, materials, System.currentTimeMillis())
                }
                val visible = materials.filter { it.type == localState.selectedType }
                val selected = localState.selectedMaterialId
                    ?.let { id -> visible.firstOrNull { it.id == id } }
                val projected = if (selected != null && summary != null) {
                    computeBalance.project(
                        summary.balance,
                        selected.type,
                        localState.amount.toFloat()
                    )
                } else {
                    null
                }
                AddToBinUiState(
                    binExists = bin != null,
                    binName = bin?.name.orEmpty(),
                    dayCount = summary?.dayCount ?: 0,
                    resting = bin?.resting ?: false,
                    selectedType = localState.selectedType,
                    materials = visible,
                    selectedMaterialId = selected?.id,
                    amount = localState.amount,
                    greenRatio = summary?.balance?.greenRatio ?: 0.5f,
                    projectedRatio = projected,
                    verdict = summary?.balance?.verdict
                        ?: com.compostheap.diary.domain.model.BalanceVerdict.BALANCED,
                    banner = localState.banner,
                    saving = localState.saving,
                    turnTick = localState.turnTick,
                    waterTick = localState.waterTick,
                    showTemperatureDialog = localState.showTemperatureDialog,
                    volumeUnit = settings.volumeUnit,
                    tempUnit = settings.tempUnit
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun selectType(type: MaterialType) {
        local.update { it.copy(selectedType = type, selectedMaterialId = null) }
    }

    fun selectMaterial(materialId: String) {
        local.update {
            val next = if (it.selectedMaterialId == materialId) null else materialId
            it.copy(selectedMaterialId = next)
        }
    }

    fun increaseAmount() {
        local.update {
            it.copy(amount = (it.amount + 1).coerceAtMost(GameConfig.MAX_AMOUNT))
        }
    }

    fun decreaseAmount() {
        local.update {
            it.copy(amount = (it.amount - 1).coerceAtLeast(GameConfig.MIN_AMOUNT))
        }
    }

    fun logSelectedEntry() {
        val current = local.value
        val materialId = current.selectedMaterialId ?: return
        if (current.saving) return
        local.update { it.copy(saving = true) }
        viewModelScope.launch {
            delay(GameConfig.SAVING_WINDOW_MS)
            logEntry(
                id = newId(),
                binId = binId,
                entryType = EntryType.ADDITION,
                materialId = materialId,
                amount = current.amount.toFloat(),
                timestampMs = System.currentTimeMillis()
            )
            local.update {
                it.copy(
                    saving = false,
                    selectedMaterialId = null,
                    amount = GameConfig.DEFAULT_AMOUNT
                )
            }
            showBanner(BannerKind.LOGGED)
        }
    }

    fun turnHeap() {
        logEntry(
            id = newId(),
            binId = binId,
            entryType = EntryType.TURN,
            materialId = null,
            amount = 0f,
            timestampMs = System.currentTimeMillis()
        )
        local.update { it.copy(turnTick = it.turnTick + 1) }
        showBanner(BannerKind.TURNED)
    }

    fun waterHeap() {
        logEntry(
            id = newId(),
            binId = binId,
            entryType = EntryType.WATER,
            materialId = null,
            amount = 0f,
            timestampMs = System.currentTimeMillis()
        )
        local.update { it.copy(waterTick = it.waterTick + 1) }
        showBanner(BannerKind.WATERED)
    }

    fun openTemperatureDialog() {
        local.update { it.copy(showTemperatureDialog = true) }
    }

    fun dismissTemperatureDialog() {
        local.update { it.copy(showTemperatureDialog = false) }
    }

    fun confirmTemperature(celsius: Float) {
        repository.addReading(
            TemperatureReading(
                id = newId(),
                binId = binId,
                celsius = celsius.coerceIn(GameConfig.MIN_CELSIUS, GameConfig.MAX_CELSIUS),
                timestampMs = System.currentTimeMillis()
            )
        )
        local.update { it.copy(showTemperatureDialog = false) }
        showBanner(BannerKind.READING)
    }

    fun toggleResting() {
        val resting = _uiState.value.resting
        repository.setResting(binId, !resting)
        showBanner(if (resting) BannerKind.ACTIVE else BannerKind.RESTING)
    }

    fun onScreenLeft() {
        bannerJob?.cancel()
        bannerJob = null
        local.update { it.copy(banner = null) }
    }

    private fun showBanner(kind: BannerKind) {
        bannerJob?.cancel()
        local.update { it.copy(banner = kind) }
        bannerJob = viewModelScope.launch {
            delay(GameConfig.BANNER_HOLD_MS)
            local.update { it.copy(banner = null) }
        }
    }

    private fun newId(): String = UUID.randomUUID().toString()
}
