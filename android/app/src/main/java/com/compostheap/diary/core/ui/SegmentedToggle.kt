package com.compostheap.diary.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilSurface

@Composable
fun RowScope.SegmentedTile(
    label: String,
    selected: Boolean,
    gradient: List<Color>,
    onClick: () -> Unit,
    iconPainter: Painter? = null,
    tileHeight: Int = 56
) {
    val shape = RoundedCornerShape(16.dp)
    val selectedLabel = stringResource(R.string.label_selected)
    val idleLabel = stringResource(R.string.label_not_selected)
    Box(
        modifier = Modifier
            .weight(1f)
            .height(tileHeight.dp)
            .shadow(if (selected) 6.dp else 0.dp, shape)
            .clip(shape)
            .background(
                if (selected) {
                    Brush.verticalGradient(gradient)
                } else {
                    Brush.verticalGradient(
                        listOf(
                            SoilSurface.copy(alpha = 0.85f),
                            SoilSurface.copy(alpha = 0.85f)
                        )
                    )
                }
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) Parchment.copy(alpha = 0.5f) else SoilEdge,
                shape = shape
            )
            .selectable(selected = selected, onClick = onClick)
            .semantics { stateDescription = if (selected) selectedLabel else idleLabel },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (iconPainter != null) {
                Image(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Parchment else ParchmentDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
