package com.compostheap.diary.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.domain.model.GardenNote
import com.compostheap.diary.domain.repository.CompostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class GardenNotesViewModel(
    private val repository: CompostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenNotesUiState())
    val uiState: StateFlow<GardenNotesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.notes, repository.batches) { notes, batches ->
                notes to batches
            }.collect { pair ->
                _uiState.update { it.copy(notes = pair.first, batches = pair.second) }
            }
        }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun confirmAdd(bedName: String, text: String, batchId: String?) {
        val trimmedBed = bedName.trim()
        if (trimmedBed.isEmpty()) return
        repository.addNote(
            GardenNote(
                id = "note_" + UUID.randomUUID().toString(),
                batchId = batchId,
                bedName = trimmedBed,
                text = text.trim(),
                createdAtMs = System.currentTimeMillis()
            )
        )
        _uiState.update { it.copy(showAddDialog = false) }
    }
}
