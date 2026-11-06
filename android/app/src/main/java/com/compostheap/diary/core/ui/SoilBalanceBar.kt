package com.compostheap.diary.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.theme.BarkDeep
import com.compostheap.diary.core.theme.BarkLight
import com.compostheap.diary.core.theme.SoilBase
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutLight
import com.compostheap.diary.core.theme.Terracotta

@Composable
fun SoilBalanceBar(
    greenRatio: Float,
    description: String,
    modifier: Modifier = Modifier,
    barHeight: Int = 18,
    projectedRatio: Float? = null
) {
    val shape = RoundedCornerShape(6.dp)
    val animated by animateFloatAsState(
        targetValue = greenRatio.coerceIn(0.04f, 0.96f),
        animationSpec = tween(GameConfig.BALANCE_SHIFT_MS, easing = FastOutSlowInEasing),
        label = "balance"
    )

    Box(
        modifier = modifier
            .height(barHeight.dp)
            .clip(shape)
            .border(1.dp, SoilEdge.copy(alpha = 0.7f), shape)
            .semantics { stateDescription = description }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(animated)
                    .fillMaxHeight()
                    .background(Brush.verticalGradient(listOf(SproutLight, SproutDeep)))
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(SoilBase)
            )
            Box(
                modifier = Modifier
                    .weight(1f - animated)
                    .fillMaxHeight()
                    .background(Brush.verticalGradient(listOf(BarkLight, BarkDeep)))
            )
        }
        if (projectedRatio != null) {
            val tick = projectedRatio.coerceIn(0.04f, 0.96f)
            Row(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(tick))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Terracotta)
                )
                Spacer(modifier = Modifier.weight(1f - tick))
            }
        }
    }
}
