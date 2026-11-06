package com.compostheap.diary.presentation.materials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.domain.repository.CompostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MaterialsListViewModel(
    private val repository: CompostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialsListUiState())
    val uiState: StateFlow<MaterialsListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.materials.collect { materials ->
                _uiState.update { it.copy(all = materials) }
            }
        }
    }

    fun selectType(type: MaterialType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun confirmAdd(name: String, type: MaterialType) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        repository.addMaterial(
            CompostMaterial(
                id = "mat_" + UUID.randomUUID().toString(),
                name = trimmed,
                type = type,
                custom = true
            )
        )
        _uiState.update { it.copy(showAddDialog = false, selectedType = type) }
    }

    fun delete(materialId: String) {
        repository.deleteMaterial(materialId)
    }
}
