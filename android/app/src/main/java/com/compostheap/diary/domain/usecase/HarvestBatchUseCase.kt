package com.compostheap.diary.domain.usecase

import com.compostheap.diary.domain.model.Batch
import com.compostheap.diary.domain.repository.CompostRepository

class HarvestBatchUseCase(
    private val repository: CompostRepository,
    private val computeBalance: ComputeBalanceUseCase
) {

    operator fun invoke(
        id: String,
        binId: String,
        yieldLitres: Float,
        note: String,
        nowMs: Long
    ): Batch? {
        val bin = repository.bins.value.firstOrNull { it.id == binId } ?: return null
        val binEntries = repository.entries.value.filter { it.binId == binId }
        val balance = computeBalance(binEntries, repository.materials.value)
        val peak = repository.readings.value
            .filter { it.binId == binId }
            .maxOfOrNull { it.celsius }
        val batch = Batch(
            id = id,
            binId = binId,
            binName = bin.name,
            startedAtMs = bin.startedAtMs,
            harvestedAtMs = nowMs,
            yieldLitres = yieldLitres,
            greenRatio = balance.greenRatio,
            peakCelsius = peak,
            note = note
        )
        repository.closeBatch(batch)
        return batch
    }

    fun estimateYield(binId: String): Float {
        val binEntries = repository.entries.value.filter { it.binId == binId }
        val balance = computeBalance(binEntries, repository.materials.value)
        val total = balance.greenVolume + balance.brownVolume
        val estimate = total * SHRINK_FACTOR
        return if (estimate < MIN_YIELD) MIN_YIELD else estimate
    }

    private companion object {
        const val SHRINK_FACTOR = 0.42f
        const val MIN_YIELD = 1f
    }
}
