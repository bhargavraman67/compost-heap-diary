package com.compostheap.diary.presentation.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.EmberGold
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.theme.WaterTeal
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.SecondaryButton
import com.compostheap.diary.core.ui.SoilBalanceBar
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.TimelineItem
import com.compostheap.diary.domain.model.VolumeUnit
import com.compostheap.diary.presentation.dialog.ConfirmHarvestDialog

private val TimelineOverlay = listOf(
    Color(0xCC2B2118),
    Color(0xE62B2118),
    Color(0xF01C150F)
)

@Composable
fun BinTimelineScreen(
    binId: String,
    onBack: () -> Unit,
    onTemperature: (String) -> Unit,
    onHarvest: (String) -> Unit
) {
    val viewModel: BinTimelineViewModel = viewModel(
        key = "timeline_$binId",
        factory = remember(binId) { AppViewModelFactory(binId) }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BinTimelineContent(
        state = state,
        onBack = onBack,
        onToggleRest = viewModel::toggleResting,
        onTemperature = { onTemperature(binId) },
        onAskHarvest = viewModel::askHarvest,
        onDismissHarvest = viewModel::dismissHarvest,
        onConfirmHarvest = {
            viewModel.dismissHarvest()
            onHarvest(binId)
        }
    )
}

@Composable
private fun BinTimelineContent(
    state: BinTimelineUiState,
    onBack: () -> Unit,
    onToggleRest: () -> Unit,
    onTemperature: () -> Unit,
    onAskHarvest: () -> Unit,
    onDismissHarvest: () -> Unit,
    onConfirmHarvest: () -> Unit
) {
    val greenPercent = Formats.percent(state.greenRatio)

    ScreenScaffold(overlay = TimelineOverlay) {
        ScreenHeader(
            title = state.binName,
            subtitle = stringResource(R.string.timeline_title),
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

        Spacer(modifier = Modifier.height(12.dp))

        OrganicCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.timeline_started) + " " +
                            Formats.dayMonth(state.startedAtMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = SoilMuted
                    )
                    Text(
                        text = stringResource(R.string.fmt_days, state.dayCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentDim
                    )
                    Text(
                        text = when {
                            state.daysSinceTurn == null -> stringResource(R.string.label_never)
                            state.daysSinceTurn == 0 -> stringResource(R.string.fmt_today)
                            else -> stringResource(R.string.fmt_days_ago, state.daysSinceTurn)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = SproutGreen
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                SoilBalanceBar(
                    greenRatio = state.greenRatio,
                    description = stringResource(
                        R.string.cd_balance,
                        greenPercent,
                        100 - greenPercent
                    ),
                    barHeight = 10,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.sprite_3),
                        contentDescription = stringResource(R.string.cd_turn_tool),
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.empty_timeline),
                        style = MaterialTheme.typography.titleMedium,
                        color = SoilMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(items = state.items, key = { it.id }) { item ->
                    TimelineRow(
                        item = item,
                        volumeUnit = state.volumeUnit,
                        tempUnit = state.tempUnit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.cta_temperature),
                onClick = onTemperature,
                iconPainter = painterResource(R.drawable.sprite_4),
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.cta_harvest),
                onClick = onAskHarvest,
                containerColor = Terracotta.copy(alpha = 0.25f),
                contentColor = Terracotta,
                borderColor = Terracotta.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.showConfirmHarvest) {
        ConfirmHarvestDialog(onConfirm = onConfirmHarvest, onDismiss = onDismissHarvest)
    }
}

@Composable
private fun TimelineRow(
    item: TimelineItem,
    volumeUnit: VolumeUnit,
    tempUnit: TempUnit
) {
    val accent = when (item.entryType) {
        EntryType.ADDITION -> if (item.materialType == MaterialType.GREEN) SproutGreen else SoilEdge
        EntryType.TURN -> ParchmentDim
        EntryType.WATER -> WaterTeal
        EntryType.READING -> EmberGold
    }
    val sprite = when (item.entryType) {
        EntryType.ADDITION ->
            if (item.materialType == MaterialType.GREEN) R.drawable.sprite_1 else R.drawable.sprite_2
        EntryType.TURN -> R.drawable.sprite_3
        EntryType.WATER -> R.drawable.sprite_2
        EntryType.READING -> R.drawable.sprite_4
    }
    val title = when (item.entryType) {
        EntryType.ADDITION -> item.materialName.orEmpty()
        EntryType.TURN -> stringResource(R.string.entry_turned)
        EntryType.WATER -> stringResource(R.string.entry_watered)
        EntryType.READING -> stringResource(R.string.entry_reading)
    }
    val unitLabel = if (volumeUnit == VolumeUnit.LITRES) {
        stringResource(R.string.unit_short_litres)
    } else {
        stringResource(R.string.unit_short_gallons)
    }
    val detail = when (item.entryType) {
        EntryType.ADDITION -> stringResource(
            R.string.fmt_volume,
            Formats.volumeValue(item.amount, volumeUnit),
            unitLabel
        )
        EntryType.READING -> {
            val celsius = item.celsius ?: 0f
            val value = Formats.temperatureValue(celsius, tempUnit)
            if (tempUnit == TempUnit.CELSIUS) {
                stringResource(R.string.fmt_celsius, value)
            } else {
                stringResource(R.string.fmt_fahrenheit, value)
            }
        }
        else -> ""
    }

    OrganicCard(modifier = Modifier.fillMaxWidth(), elevation = 3) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(SoilEdge)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(sprite),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Parchment,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (detail.isNotEmpty()) {
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formats.dayMonth(item.timestampMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = SoilMuted
                )
                Text(
                    text = Formats.time(item.timestampMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = SoilMuted
                )
            }
        }
    }
}
