package com.compostheap.diary.presentation.materials

import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.MaterialType

data class MaterialsListUiState(
    val all: List<CompostMaterial> = emptyList(),
    val selectedType: MaterialType = MaterialType.GREEN,
    val showAddDialog: Boolean = false
) {
    val visible: List<CompostMaterial> get() = all.filter { it.type == selectedType }
}
