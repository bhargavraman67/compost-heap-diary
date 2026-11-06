package com.compostheap.diary.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.BarkDeep
import com.compostheap.diary.core.theme.BarkLight
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.theme.WaterTeal
import com.compostheap.diary.core.ui.MaterialChip
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.PrimaryButton
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.SecondaryButton
import com.compostheap.diary.core.ui.SegmentedTile
import com.compostheap.diary.core.ui.SoilBalanceBar
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.BalanceVerdict
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.domain.model.VolumeUnit
import com.compostheap.diary.presentation.dialog.TemperatureDialog
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch

private val GameOverlay = listOf(
    Color(0x992B2118),
    Color(0xCC2B2118),
    Color(0xF01C150F)
)

@Composable
fun AddToBinScreen(
    binId: String,
    onBack: () -> Unit,
    onTimeline: (String) -> Unit,
    onHarvest: (String) -> Unit
) {
    val viewModel: AddToBinViewModel = viewModel(
        key = "addToBin_$binId",
        factory = remember(binId) { AppViewModelFactory(binId) }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { viewModel.onScreenLeft() }
    }

    AddToBinContent(
        state = state,
        onBack = onBack,
        onToggleRest = viewModel::toggleResting,
        onSelectType = viewModel::selectType,
        onSelectMaterial = viewModel::selectMaterial,
        onIncrease = viewModel::increaseAmount,
        onDecrease = viewModel::decreaseAmount,
        onLog = viewModel::logSelectedEntry,
        onTurn = viewModel::turnHeap,
        onWater = viewModel::waterHeap,
        onOpenTemperature = viewModel::openTemperatureDialog,
        onDismissTemperature = viewModel::dismissTemperatureDialog,
        onConfirmTemperature = viewModel::confirmTemperature,
        onTimeline = { onTimeline(binId) },
        onHarvest = { onHarvest(binId) }
    )
}

@Composable
private fun AddToBinContent(
    state: AddToBinUiState,
    onBack: () -> Unit,
    onToggleRest: () -> Unit,
    onSelectType: (MaterialType) -> Unit,
    onSelectMaterial: (String) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onLog: () -> Unit,
    onTurn: () -> Unit,
    onWater: () -> Unit,
    onOpenTemperature: () -> Unit,
    onDismissTemperature: () -> Unit,
    onConfirmTemperature: (Float) -> Unit,
    onTimeline: () -> Unit,
    onHarvest: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val greenPercent = Formats.percent(state.greenRatio)
    val unitLabel = if (state.volumeUnit == VolumeUnit.LITRES) {
        stringResource(R.string.unit_short_litres)
    } else {
        stringResource(R.string.unit_short_gallons)
    }

    ScreenScaffold(
        overlay = GameOverlay,
        floating = {
            TurnSwirl(
                tick = state.turnTick,
                modifier = Modifier.align(Alignment.Center)
            )
            WaterRipple(
                tick = state.waterTick,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    ) {
        ScreenHeader(
            title = state.binName,
            subtitle = stringResource(R.string.game_day, state.dayCount),
            onBack = onBack,
            trailing = {
                IconButton(onClick = onToggleRest, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_rest_batch),
                        modifier = Modifier.size(26.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        )

        AnimatedVisibility(
            visible = state.banner != null,
            enter = expandVertically(animationSpec = tween(180)) + fadeIn(tween(180)),
            exit = shrinkVertically(animationSpec = tween(180)) + fadeOut(tween(180))
        ) {
            Banner(kind = state.banner)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SegmentedTile(
                label = stringResource(R.string.label_greens),
                selected = state.selectedType == MaterialType.GREEN,
                gradient = listOf(SproutGreen, SproutDeep),
                onClick = { onSelectType(MaterialType.GREEN) },
                iconPainter = painterResource(R.drawable.sprite_1),
                tileHeight = 52
            )
            SegmentedTile(
                label = stringResource(R.string.label_browns),
                selected = state.selectedType == MaterialType.BROWN,
                gradient = listOf(BarkLight, BarkDeep),
                onClick = { onSelectType(MaterialType.BROWN) },
                iconPainter = painterResource(R.drawable.sprite_2),
                tileHeight = 52
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(items = state.materials, key = { it.id }) { material ->
                MaterialChip(
                    name = material.name,
                    selected = material.id == state.selectedMaterialId,
                    accent = if (state.selectedType == MaterialType.GREEN) {
                        SproutGreen
                    } else {
                        Terracotta
                    },
                    onClick = { onSelectMaterial(material.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AmountStepper(
            amount = state.amount,
            unitLabel = unitLabel,
            volumeUnit = state.volumeUnit,
            onIncrease = onIncrease,
            onDecrease = onDecrease
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.cta_turn),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTurn()
                },
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.cta_water),
                onClick = onWater,
                contentColor = WaterTeal,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.cta_add_reading),
                onClick = onOpenTemperature,
                iconPainter = painterResource(R.drawable.sprite_4),
                modifier = Modifier.weight(1.4f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        BalanceStrip(
            greenRatio = state.greenRatio,
            projectedRatio = state.projectedRatio,
            greenPercent = greenPercent,
            verdict = state.verdict
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (!state.canLog) {
            Text(
                text = stringResource(R.string.select_material),
                style = MaterialTheme.typography.labelSmall,
                color = SoilMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        PrimaryButton(
            text = stringResource(R.string.cta_log_entry),
            onClick = onLog,
            enabled = state.canLog,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextButton(
                onClick = onTimeline,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.cta_timeline),
                    style = MaterialTheme.typography.labelLarge,
                    color = ParchmentDim
                )
            }
            SecondaryButton(
                text = stringResource(R.string.cta_harvest),
                onClick = onHarvest,
                containerColor = Terracotta.copy(alpha = 0.22f),
                contentColor = Terracotta,
                borderColor = Terracotta.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.showTemperatureDialog) {
        TemperatureDialog(
            tempUnit = state.tempUnit,
            onConfirm = onConfirmTemperature,
            onDismiss = onDismissTemperature
        )
    }
}

@Composable
private fun Banner(kind: BannerKind?) {
    val text = when (kind) {
        BannerKind.LOGGED -> stringResource(R.string.banner_logged)
        BannerKind.TURNED -> stringResource(R.string.banner_turned)
        BannerKind.WATERED -> stringResource(R.string.banner_watered)
        BannerKind.READING -> stringResource(R.string.banner_reading)
        BannerKind.RESTING -> stringResource(R.string.banner_resting)
        BannerKind.ACTIVE -> stringResource(R.string.banner_active)
        null -> stringResource(R.string.banner_logged)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SproutGreen.copy(alpha = 0.18f))
            .border(1.dp, SproutGreen.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = SproutGreen,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        )
    }
}

@Composable
private fun AmountStepper(
    amount: Int,
    unitLabel: String,
    volumeUnit: VolumeUnit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val decreaseLabel = stringResource(R.string.cd_decrease)
    val increaseLabel = stringResource(R.string.cd_increase)
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
                enabled = amount > GameConfig.MIN_AMOUNT,
                modifier = Modifier
                    .size(56.dp)
                    .semantics { contentDescription = decreaseLabel }
            ) {
                Text(
                    text = stringResource(R.string.cta_minus),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = Formats.volumeValue(amount.toFloat(), volumeUnit),
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
                enabled = amount < GameConfig.MAX_AMOUNT,
                modifier = Modifier
                    .size(56.dp)
                    .semantics { contentDescription = increaseLabel }
            ) {
                Text(
                    text = stringResource(R.string.cta_plus),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun BalanceStrip(
    greenRatio: Float,
    projectedRatio: Float?,
    greenPercent: Int,
    verdict: BalanceVerdict
) {
    val verdictLabel = when (verdict) {
        BalanceVerdict.TOO_WET -> stringResource(R.string.label_too_wet)
        BalanceVerdict.TOO_DRY -> stringResource(R.string.label_too_dry)
        BalanceVerdict.BALANCED -> stringResource(R.string.label_balanced)
    }
    val hint = when (verdict) {
        BalanceVerdict.TOO_WET -> stringResource(R.string.hint_add_browns)
        BalanceVerdict.TOO_DRY -> stringResource(R.string.hint_add_greens)
        BalanceVerdict.BALANCED -> stringResource(R.string.hint_keep_going)
    }
    val verdictColor = when (verdict) {
        BalanceVerdict.TOO_WET -> Terracotta
        BalanceVerdict.TOO_DRY -> ParchmentDim
        BalanceVerdict.BALANCED -> SproutGreen
    }

    OrganicCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.label_greens) + " " +
                        stringResource(R.string.fmt_percent, greenPercent),
                    style = MaterialTheme.typography.labelSmall,
                    color = SproutGreen
                )
                Text(
                    text = verdictLabel + " " + hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = verdictColor
                )
                Text(
                    text = stringResource(R.string.label_browns) + " " +
                        stringResource(R.string.fmt_percent, 100 - greenPercent),
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentDim
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            SoilBalanceBar(
                greenRatio = greenRatio,
                projectedRatio = projectedRatio,
                description = stringResource(
                    R.string.cd_balance,
                    greenPercent,
                    100 - greenPercent
                ),
                barHeight = 12,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TurnSwirl(tick: Int, modifier: Modifier = Modifier) {
    val angle = remember { Animatable(0f) }
    val radius = remember { Animatable(0f) }
    val fade = remember { Animatable(0f) }

    LaunchedEffect(tick) {
        if (tick <= 0) return@LaunchedEffect
        angle.snapTo(0f)
        radius.snapTo(0f)
        fade.snapTo(1f)
        launch {
            angle.animateTo(
                targetValue = 360f,
                animationSpec = tween(GameConfig.TURN_SWIRL_MS, easing = FastOutSlowInEasing)
            )
        }
        launch {
            radius.animateTo(46f, tween(GameConfig.TURN_SWIRL_MS / 2))
            radius.animateTo(28f, tween(GameConfig.TURN_SWIRL_MS / 2))
        }
        fade.animateTo(0f, tween(GameConfig.TURN_SWIRL_MS))
    }

    if (fade.value <= 0.01f) return

    Box(modifier = modifier.size(140.dp), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )
        repeat(10) { index ->
            val step = angle.value + index * 36f
            val rad = step * Math.PI.toFloat() / 180f
            Box(
                modifier = Modifier
                    .offset(
                        x = (cos(rad) * radius.value).dp,
                        y = (sin(rad) * radius.value).dp
                    )
                    .size((4 + index % 4).dp)
                    .clip(CircleShape)
                    .background(
                        if (index % 2 == 0) {
                            SproutGreen.copy(alpha = fade.value)
                        } else {
                            SoilEdge.copy(alpha = fade.value)
                        }
                    )
            )
        }
    }
}

@Composable
private fun WaterRipple(tick: Int, modifier: Modifier = Modifier) {
    val spread = remember { Animatable(0f) }
    val fade = remember { Animatable(0f) }

    LaunchedEffect(tick) {
        if (tick <= 0) return@LaunchedEffect
        spread.snapTo(0f)
        fade.snapTo(0.8f)
        launch { spread.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }
        fade.animateTo(0f, tween(400))
    }

    if (fade.value <= 0.01f) return

    Box(
        modifier = modifier
            .size((60 + spread.value * 120).dp)
            .clip(CircleShape)
            .background(WaterTeal.copy(alpha = fade.value * 0.35f))
            .border(2.dp, WaterTeal.copy(alpha = fade.value), CircleShape)
    ) {
    }
}
