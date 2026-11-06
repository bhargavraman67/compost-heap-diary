package com.compostheap.diary.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.theme.SoilBase

@Composable
fun ScreenScaffold(
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 20,
    floating: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SoilBase)
    ) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        AnimatedVisibility(
            visibleState = visibleState,
            modifier = Modifier.fillMaxSize(),
            enter = slideInVertically(
                animationSpec = tween(GameConfig.RISE_MS, easing = FastOutSlowInEasing)
            ) { full -> full / 12 } + fadeIn(animationSpec = tween(GameConfig.RISE_MS)),
            label = "rise"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = horizontalPadding.dp),
                content = content
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = horizontalPadding.dp),
            content = floating
        )
    }
}
