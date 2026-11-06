package com.compostheap.diary.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilBase
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.SproutGreen

private data class Particle(
    val startX: Int,
    val size: Int,
    val duration: Int,
    val travel: Int,
    val green: Boolean,
    val alpha: Float
)

private val particles = listOf(
    Particle(-128, 5, 3200, 260, false, 0.26f),
    Particle(-74, 3, 2600, 300, true, 0.30f),
    Particle(-18, 6, 4200, 240, false, 0.18f),
    Particle(26, 4, 3600, 320, true, 0.22f),
    Particle(78, 5, 2900, 280, false, 0.24f),
    Particle(122, 3, 3900, 300, true, 0.20f),
    Particle(150, 4, 3400, 250, false, 0.32f)
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val finished by viewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splash")
    val breath by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.045f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.BREATH_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )
    val ring by transition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.RING_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring"
    )

    Box(
        modifier = Modifier
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
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xE61C150F),
                            Color(0xF21C150F),
                            Color(0xFA120D08)
                        )
                    )
                )
        )

        particles.forEachIndexed { index, particle ->
            val drift by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "particle$index"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = particle.startX.dp,
                        y = (-160 + (drift * particle.travel)).dp
                    )
                    .size(particle.size.dp)
                    .clip(CircleShape)
                    .background(
                        if (particle.green) {
                            SproutGreen.copy(alpha = particle.alpha * (1f - drift))
                        } else {
                            SoilEdge.copy(alpha = particle.alpha * (1f - drift))
                        }
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(188.dp)
                    .graphicsLayer {
                        scaleX = breath
                        scaleY = breath
                    }
                    .clip(CircleShape)
                    .background(SoilSurface.copy(alpha = 0.55f))
                    .border(2.dp, SproutGreen.copy(alpha = ring), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(148.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                color = Parchment,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.labelMedium,
                color = SoilMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(168.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SproutGreen,
                trackColor = Color(0xFF3A2C20)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = SoilMuted
            )
        }
    }
}
