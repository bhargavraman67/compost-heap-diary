package com.compostheap.diary.domain.usecase

import com.compostheap.diary.domain.model.Balance
import com.compostheap.diary.domain.model.BalanceVerdict
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.Entry
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.model.MaterialType

class ComputeBalanceUseCase {

    operator fun invoke(entries: List<Entry>, materials: List<CompostMaterial>): Balance {
        val byId = materials.associateBy { it.id }
        var green = 0f
        var brown = 0f
        entries.forEach { entry ->
            if (entry.entryType != EntryType.ADDITION) return@forEach
            val type = entry.materialId?.let { byId[it] }?.type ?: return@forEach
            if (type == MaterialType.GREEN) green += entry.amount else brown += entry.amount
        }
        val total = green + brown
        val ratio = if (total <= 0f) 0.5f else green / total
        val verdict = when {
            total <= 0f -> BalanceVerdict.BALANCED
            ratio > WET_THRESHOLD -> BalanceVerdict.TOO_WET
            ratio < DRY_THRESHOLD -> BalanceVerdict.TOO_DRY
            else -> BalanceVerdict.BALANCED
        }
        return Balance(green, brown, ratio, verdict)
    }

    fun project(current: Balance, addedType: MaterialType, amount: Float): Float {
        val green = current.greenVolume + if (addedType == MaterialType.GREEN) amount else 0f
        val brown = current.brownVolume + if (addedType == MaterialType.BROWN) amount else 0f
        val total = green + brown
        return if (total <= 0f) 0.5f else green / total
    }

    private companion object {
        const val WET_THRESHOLD = 0.55f
        const val DRY_THRESHOLD = 0.30f
    }
}
