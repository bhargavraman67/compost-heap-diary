package com.compostheap.diary.presentation.timeline

import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.TimelineItem
import com.compostheap.diary.domain.model.VolumeUnit

data class BinTimelineUiState(
    val binName: String = "",
    val startedAtMs: Long = 0L,
    val dayCount: Int = 0,
    val daysSinceTurn: Int? = null,
    val resting: Boolean = false,
    val greenRatio: Float = 0.5f,
    val items: List<TimelineItem> = emptyList(),
    val showConfirmHarvest: Boolean = false,
    val volumeUnit: VolumeUnit = VolumeUnit.LITRES,
    val tempUnit: TempUnit = TempUnit.CELSIUS
)
