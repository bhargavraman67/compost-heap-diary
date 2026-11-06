package com.compostheap.diary.presentation.notes

import com.compostheap.diary.domain.model.Batch
import com.compostheap.diary.domain.model.GardenNote

data class GardenNotesUiState(
    val notes: List<GardenNote> = emptyList(),
    val batches: List<Batch> = emptyList(),
    val showAddDialog: Boolean = false
)
