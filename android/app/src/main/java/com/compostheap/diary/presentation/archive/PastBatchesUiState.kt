package com.compostheap.diary.presentation.archive

import com.compostheap.diary.domain.model.Batch
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.VolumeUnit

data class PastBatchesUiState(
    val batches: List<Batch> = emptyList(),
    val averageDays: Int = 0,
    val totalYieldLitres: Float = 0f,
    val selectedBatchId: String? = null,
    val volumeUnit: VolumeUnit = VolumeUnit.LITRES,
    val tempUnit: TempUnit = TempUnit.CELSIUS
) {
    val selected: Batch? get() = batches.firstOrNull { it.id == selectedBatchId }
}
