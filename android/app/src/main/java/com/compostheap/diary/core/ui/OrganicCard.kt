package com.compostheap.diary.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.compostheap.diary.core.theme.OrganicShape
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilSurface

@Composable
fun OrganicCard(
    modifier: Modifier = Modifier,
    containerColor: Color = SoilSurface,
    borderColor: Color = SoilEdge,
    shape: Shape = OrganicShape,
    elevation: Int = 6,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation.dp, shape)
            .clip(shape)
            .background(containerColor)
            .border(1.dp, borderColor, shape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            SoilEdge.copy(alpha = 0.10f),
                            Color.Transparent,
                            SoilEdge.copy(alpha = 0.10f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0x33000000), Color.Transparent))
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0x1AF1E9DA)))
                )
        )
        content()
    }
}
