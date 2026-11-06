package com.compostheap.diary.presentation.archive

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.PillShape
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.SoilBalanceBar
import com.compostheap.diary.core.ui.StatCard
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.VolumeUnit
import com.compostheap.diary.presentation.dialog.BatchDetailDialog

private val ArchiveOverlay = listOf(
    Color(0xD92B2118),
    Color(0xE62B2118),
    Color(0xF21C150F)
)

@Composable
fun PastBatchesScreen(onBack: () -> Unit) {
    val viewModel: PastBatchesViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PastBatchesContent(
        state = state,
        onBack = onBack,
        onSelect = viewModel::select,
        onDismissDetail = viewModel::dismissDetail
    )
}

@Composable
private fun PastBatchesContent(
    state: PastBatchesUiState,
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onDismissDetail: () -> Unit
) {
    val unitLabel = if (state.volumeUnit == VolumeUnit.LITRES) {
        stringResource(R.string.unit_short_litres)
    } else {
        stringResource(R.string.unit_short_gallons)
    }

    ScreenScaffold(overlay = ArchiveOverlay) {
        ScreenHeader(
            title = stringResource(R.string.batches_title),
            subtitle = stringResource(R.string.harvest_hint),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.batches.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.stat_batches),
                    value = state.batches.size.toString(),
                    accent = ParchmentDim,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.stat_avg_days),
                    value = state.averageDays.toString(),
                    accent = Terracotta,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.stat_total_yield),
                    value = stringResource(
                        R.string.fmt_volume,
                        Formats.volumeValue(state.totalYieldLitres, state.volumeUnit),
                        unitLabel
                    ),
                    accent = SproutGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.batches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_seedling),
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.empty_batches),
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(items = state.batches, key = { it.id }) { batch ->
                    val days = ((batch.harvestedAtMs - batch.startedAtMs) / GameConfig.DAY_MS).toInt()
                    val greenPercent = Formats.percent(batch.greenRatio)
                    OrganicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(batch.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.sprite_5),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = batch.binName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Parchment,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = Formats.dayMonthYear(batch.harvestedAtMs),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoilMuted
                                )
                                if (batch.note.isNotEmpty()) {
                                    Text(
                                        text = batch.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ParchmentDim,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                SoilBalanceBar(
                                    greenRatio = batch.greenRatio,
                                    description = stringResource(
                                        R.string.cd_balance,
                                        greenPercent,
                                        100 - greenPercent
                                    ),
                                    barHeight = 6,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(PillShape)
                                        .background(SproutGreen.copy(alpha = 0.22f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.fmt_days, days),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SproutGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = stringResource(
                                        R.string.fmt_volume,
                                        Formats.volumeValue(batch.yieldLitres, state.volumeUnit),
                                        unitLabel
                                    ),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = ParchmentDim
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    val selected = state.selected
    if (selected != null) {
        BatchDetailDialog(
            batch = selected,
            volumeUnit = state.volumeUnit,
            onDismiss = onDismissDetail
        )
    }
}
