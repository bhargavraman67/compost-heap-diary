package com.compostheap.diary.domain.model

data class CompostMaterial(
    val id: String,
    val name: String,
    val type: MaterialType,
    val custom: Boolean
)

data class Entry(
    val id: String,
    val binId: String,
    val entryType: EntryType,
    val materialId: String?,
    val amount: Float,
    val timestampMs: Long
)

data class TemperatureReading(
    val id: String,
    val binId: String,
    val celsius: Float,
    val timestampMs: Long
)

data class Bin(
    val id: String,
    val name: String,
    val startedAtMs: Long,
    val lastTurnedAtMs: Long?,
    val resting: Boolean
)

data class Batch(
    val id: String,
    val binId: String,
    val binName: String,
    val startedAtMs: Long,
    val harvestedAtMs: Long,
    val yieldLitres: Float,
    val greenRatio: Float,
    val peakCelsius: Float?,
    val note: String
)

data class GardenNote(
    val id: String,
    val batchId: String?,
    val bedName: String,
    val text: String,
    val createdAtMs: Long
)

data class AppSettings(
    val volumeUnit: VolumeUnit,
    val tempUnit: TempUnit,
    val theme: ThemeVariant
)

data class Balance(
    val greenVolume: Float,
    val brownVolume: Float,
    val greenRatio: Float,
    val verdict: BalanceVerdict
)

data class BinSummary(
    val bin: Bin,
    val balance: Balance,
    val dayCount: Int,
    val entryCount: Int,
    val daysSinceTurn: Int?,
    val progress: Float,
    val ready: Boolean
)

data class TimelineItem(
    val id: String,
    val entryType: EntryType,
    val materialName: String?,
    val materialType: MaterialType?,
    val amount: Float,
    val celsius: Float?,
    val timestampMs: Long
)

data class DiarySnapshot(
    val bins: List<Bin>,
    val entries: List<Entry>,
    val readings: List<TemperatureReading>,
    val materials: List<CompostMaterial>,
    val batches: List<Batch>,
    val notes: List<GardenNote>
)
