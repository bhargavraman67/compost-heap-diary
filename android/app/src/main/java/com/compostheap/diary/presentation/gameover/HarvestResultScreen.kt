package com.compostheap.diary.presentation.gameover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.EmberGold
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.PrimaryButton
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.SecondaryButton
import com.compostheap.diary.core.ui.StatCard
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.VolumeUnit
import kotlinx.coroutines.launch

private val HarvestOverlay = listOf(
    Color(0xCC2B2118),
    Color(0x99E2725B),
    Color(0xF21C150F)
)

private data class HarvestStat(val label: String, val value: String, val accent: Color)

@Composable
fun HarvestResultScreen(
    binId: String,
    onPlayAgain: (String) -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: HarvestRecordViewModel = viewModel(
        key = "harvest_$binId",
        factory = remember(binId) { AppViewModelFactory(binId) }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HarvestRecordContent(
        state = state,
        onNoteChange = viewModel::updateNote,
        onIncrease = viewModel::increaseYield,
        onDecrease = viewModel::decreaseYield,
        onPlayAgain = {
            viewModel.commit()
            onPlayAgain(binId)
        },
        onMenu = {
            viewModel.commit()
            onMenu()
        }
    )
}

@Composable
private fun HarvestRecordContent(
    state: HarvestRecordUiState,
    onNoteChange: (String) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val unitLabel = if (state.volumeUnit == VolumeUnit.LITRES) {
        stringResource(R.string.unit_short_litres)
    } else {
        stringResource(R.string.unit_short_gallons)
    }

    val peak = state.peakCelsius
    val peakValue = if (peak == null) {
        ""
    } else if (state.tempUnit == TempUnit.CELSIUS) {
        stringResource(R.string.fmt_celsius, Formats.temperatureValue(peak, state.tempUnit))
    } else {
        stringResource(R.string.fmt_fahrenheit, Formats.temperatureValue(peak, state.tempUnit))
    }
    val daysStat = HarvestStat(
        label = stringResource(R.string.stat_days),
        value = state.dayCount.toString(),
        accent = ParchmentDim
    )
    val yieldStat = HarvestStat(
        label = stringResource(R.string.stat_yield),
        value = stringResource(
            R.string.fmt_volume,
            Formats.volumeValue(state.yieldLitres, state.volumeUnit),
            unitLabel
        ),
        accent = SproutGreen
    )
    val peakStat = HarvestStat(
        label = stringResource(R.string.stat_peak_c),
        value = peakValue,
        accent = EmberGold
    )
    val stats = listOfNotNull(
        daysStat.takeIf { state.dayCount > 0 },
        yieldStat.takeIf { state.yieldLitres > 0f },
        peakStat.takeIf { peak != null && peak > 0f }
    )

    ScreenScaffold(overlay = HarvestOverlay) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.harvest_eyebrow),
            style = MaterialTheme.typography.labelMedium,
            color = Terracotta,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.harvest_title),
            style = MaterialTheme.typography.displaySmall,
            color = Parchment,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { liveRegion = LiveRegionMode.Polite }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = state.binName,
            style = MaterialTheme.typography.titleMedium,
            color = ParchmentDim,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Seedling()

        if (stats.size >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stats.forEach { stat ->
                    StatCard(
                        label = stat.label,
                        value = stat.value,
                        accent = stat.accent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        YieldStepper(
            amount = Formats.volumeValue(state.yieldLitres, state.volumeUnit),
            unitLabel = unitLabel,
            onIncrease = onIncrease,
            onDecrease = onDecrease
        )

        Spacer(modifier = Modifier.height(12.dp))

        OrganicCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.note,
                onValueChange = onNoteChange,
                minLines = 2,
                maxLines = 2,
                placeholder = {
                    Text(
                        text = stringResource(R.string.note_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoilMuted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.harvest_hint),
            style = MaterialTheme.typography.bodySmall,
            color = SoilMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        PrimaryButton(
            text = stringResource(R.string.cta_again),
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        SecondaryButton(
            text = stringResource(R.string.cta_menu),
            onClick = onMenu,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun Seedling() {
    val grow = remember { Animatable(0f) }
    val wobble = remember { Animatable(-6f) }
    val glow = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { glow.animateTo(1f, tween(GameConfig.SEEDLING_MS)) }
        launch {
            grow.animateTo(
                targetValue = 1f,
                animationSpec = tween(GameConfig.SEEDLING_MS, easing = FastOutSlowInEasing)
            )
            wobble.animateTo(0f, tween(240))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            SproutGreen.copy(alpha = 0.20f * glow.value),
                            Color.Transparent
                        )
                    )
                )
        )
        Image(
            painter = painterResource(R.drawable.sprite_5),
            contentDescription = stringResource(R.string.cd_seedling),
            modifier = Modifier
                .size(112.dp)
                .graphicsLayer {
                    scaleX = grow.value
                    scaleY = grow.value
                    rotationZ = wobble.value
                    transformOrigin = TransformOrigin(0.5f, 1f)
                },
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun YieldStepper(
    amount: String,
    unitLabel: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    OrganicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onDecrease,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.cta_minus),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.displaySmall,
                    color = Parchment,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unitLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentDim,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            OutlinedButton(
                onClick = onIncrease,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.cta_plus),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
