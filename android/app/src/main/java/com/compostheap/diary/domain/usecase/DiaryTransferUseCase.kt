package com.compostheap.diary.domain.usecase

import com.compostheap.diary.domain.repository.CompostRepository

class ExportDiaryUseCase(private val repository: CompostRepository) {

    operator fun invoke(): String = repository.exportSnapshot()
}

class ImportDiaryUseCase(private val repository: CompostRepository) {

    operator fun invoke(raw: String): Boolean {
        if (raw.isBlank()) return false
        return repository.importSnapshot(raw.trim())
    }
}
