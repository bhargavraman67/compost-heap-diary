package com.compostheap.diary.domain.usecase

import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.Entry
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.model.TemperatureReading
import com.compostheap.diary.domain.model.TimelineItem

class BuildTimelineUseCase {

    operator fun invoke(
        binId: String,
        entries: List<Entry>,
        readings: List<TemperatureReading>,
        materials: List<CompostMaterial>
    ): List<TimelineItem> {
        val byId = materials.associateBy { it.id }
        val fromEntries = entries.filter { it.binId == binId }.map { entry ->
            val material = entry.materialId?.let { byId[it] }
            TimelineItem(
                id = entry.id,
                entryType = entry.entryType,
                materialName = material?.name,
                materialType = material?.type,
                amount = entry.amount,
                celsius = null,
                timestampMs = entry.timestampMs
            )
        }
        val fromReadings = readings.filter { it.binId == binId }.map { reading ->
            TimelineItem(
                id = reading.id,
                entryType = EntryType.READING,
                materialName = null,
                materialType = null,
                amount = 0f,
                celsius = reading.celsius,
                timestampMs = reading.timestampMs
            )
        }
        return (fromEntries + fromReadings).sortedByDescending { it.timestampMs }
    }
}
