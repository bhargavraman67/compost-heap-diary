package com.compostheap.diary.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.OrganicShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilSurface

@Composable
fun MaterialChip(
    name: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLabel = stringResource(R.string.label_selected)
    val idleLabel = stringResource(R.string.label_not_selected)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .shadow(if (selected) 6.dp else 2.dp, OrganicShape)
            .clip(OrganicShape)
            .background(if (selected) accent.copy(alpha = 0.18f) else SoilSurface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accent else SoilEdge,
                shape = OrganicShape
            )
            .selectable(selected = selected, onClick = onClick)
            .semantics { stateDescription = if (selected) selectedLabel else idleLabel },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Parchment else ParchmentDim,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
        )
    }
}
