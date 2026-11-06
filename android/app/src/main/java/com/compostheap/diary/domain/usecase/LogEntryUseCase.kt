package com.compostheap.diary.domain.usecase

import com.compostheap.diary.domain.model.Entry
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.repository.CompostRepository

class LogEntryUseCase(private val repository: CompostRepository) {

    operator fun invoke(
        id: String,
        binId: String,
        entryType: EntryType,
        materialId: String?,
        amount: Float,
        timestampMs: Long
    ): Boolean {
        if (entryType == EntryType.ADDITION && (materialId == null || amount <= 0f)) return false
        repository.addEntry(
            Entry(
                id = id,
                binId = binId,
                entryType = entryType,
                materialId = materialId,
                amount = if (entryType == EntryType.ADDITION) amount else 0f,
                timestampMs = timestampMs
            )
        )
        return true
    }
}
