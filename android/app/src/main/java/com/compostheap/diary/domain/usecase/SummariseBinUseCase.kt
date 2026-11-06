package com.compostheap.diary.domain.usecase

import com.compostheap.diary.domain.model.Bin
import com.compostheap.diary.domain.model.BinSummary
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.Entry

class SummariseBinUseCase(private val computeBalance: ComputeBalanceUseCase) {

    operator fun invoke(
        bin: Bin,
        entries: List<Entry>,
        materials: List<CompostMaterial>,
        nowMs: Long
    ): BinSummary {
        val binEntries = entries.filter { it.binId == bin.id }
        val balance = computeBalance(binEntries, materials)
        val dayCount = daysBetween(bin.startedAtMs, nowMs)
        val sinceTurn = bin.lastTurnedAtMs?.let { daysBetween(it, nowMs) }
        val progress = (dayCount.toFloat() / MATURITY_DAYS).coerceIn(0f, 1f)
        return BinSummary(
            bin = bin,
            balance = balance,
            dayCount = dayCount,
            entryCount = binEntries.size,
            daysSinceTurn = sinceTurn,
            progress = progress,
            ready = dayCount >= MATURITY_DAYS
        )
    }

    fun daysBetween(fromMs: Long, toMs: Long): Int {
        val delta = toMs - fromMs
        if (delta <= 0L) return 0
        return (delta / DAY_MS).toInt()
    }

    private companion object {
        const val MATURITY_DAYS = 90
        const val DAY_MS = 86_400_000L
    }
}
