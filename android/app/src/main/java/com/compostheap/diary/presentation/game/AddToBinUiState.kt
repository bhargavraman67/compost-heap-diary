package com.compostheap.diary.presentation.game

import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.domain.model.BalanceVerdict
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.VolumeUnit

enum class BannerKind { LOGGED, TURNED, WATERED, READING, RESTING, ACTIVE }

data class AddToBinUiState(
    val binExists: Boolean = false,
    val binName: String = "",
    val dayCount: Int = 0,
    val resting: Boolean = false,
    val selectedType: MaterialType = MaterialType.GREEN,
    val materials: List<CompostMaterial> = emptyList(),
    val selectedMaterialId: String? = null,
    val amount: Int = GameConfig.DEFAULT_AMOUNT,
    val greenRatio: Float = 0.5f,
    val projectedRatio: Float? = null,
    val verdict: BalanceVerdict = BalanceVerdict.BALANCED,
    val banner: BannerKind? = null,
    val saving: Boolean = false,
    val turnTick: Int = 0,
    val waterTick: Int = 0,
    val showTemperatureDialog: Boolean = false,
    val volumeUnit: VolumeUnit = VolumeUnit.LITRES,
    val tempUnit: TempUnit = TempUnit.CELSIUS
) {
    val canLog: Boolean get() = selectedMaterialId != null && !saving
}
