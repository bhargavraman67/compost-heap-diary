package com.compostheap.diary.presentation.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.compostheap.diary.core.theme.OrganicShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.ui.BinCard
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.SecondaryButton
import com.compostheap.diary.core.ui.SoilBalanceBar
import com.compostheap.diary.core.ui.StatCard
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.BalanceVerdict
import com.compostheap.diary.presentation.dialog.AddBinDialog

private val MenuOverlay = listOf(
    Color(0xB32B2118),
    Color(0xD92B2118),
    Color(0xF21C150F)
)

@Composable
fun BinYardScreen(
    onStartLogging: (String) -> Unit,
    onOpenBin: (String) -> Unit,
    onMaterials: () -> Unit,
    onBatches: () -> Unit,
    onNotes: () -> Unit,
    onSettings: () -> Unit
) {
    val viewModel: BinYardViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BinYardContent(
        state = state,
        onStart = {
            val target = state.summaries.firstOrNull { !it.bin.resting } ?: state.summaries.firstOrNull()
            if (target == null) viewModel.openAddBin() else onStartLogging(target.bin.id)
        },
        onOpenBin = onOpenBin,
        onMaterials = onMaterials,
        onBatches = onBatches,
        onNotes = onNotes,
        onSettings = onSettings,
        onAddBin = viewModel::openAddBin,
        onDismissAddBin = viewModel::dismissAddBin,
        onConfirmAddBin = viewModel::confirmAddBin
    )
}

@Composable
private fun BinYardContent(
    state: BinYardUiState,
    onStart: () -> Unit,
    onOpenBin: (String) -> Unit,
    onMaterials: () -> Unit,
    onBatches: () -> Unit,
    onNotes: () -> Unit,
    onSettings: () -> Unit,
    onAddBin: () -> Unit,
    onDismissAddBin: () -> Unit,
    onConfirmAddBin: (String) -> Unit
) {
    val greenPercent = Formats.percent(state.greenRatio)
    val verdictLabel = when (state.verdict) {
        BalanceVerdict.TOO_WET -> stringResource(R.string.label_too_wet)
        BalanceVerdict.TOO_DRY -> stringResource(R.string.label_too_dry)
        BalanceVerdict.BALANCED -> stringResource(R.string.label_balanced)
    }
    val verdictColor = when (state.verdict) {
        BalanceVerdict.TOO_WET -> Terracotta
        BalanceVerdict.TOO_DRY -> ParchmentDim
        BalanceVerdict.BALANCED -> SproutGreen
    }

    ScreenScaffold(
        overlay = MenuOverlay,
        floating = {
            IconButton(
                onClick = onAddBin,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 84.dp)
                    .shadow(8.dp, CircleShape)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(SproutGreen, SproutDeep)))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.cd_add_bin),
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF1C150F)
                )
            }
        }
    ) {
        ScreenHeader(
            title = stringResource(R.string.yard_title),
            subtitle = stringResource(R.string.yard_subtitle),
            trailing = {
                IconButton(onClick = onSettings, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = ParchmentDim,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val showTurns = state.turnsThisMonth > 0
        val showBins = state.activeBins > 0

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StartTile(
                onClick = onStart,
                modifier = Modifier
                    .weight(if (showBins || showTurns) 1.55f else 1f)
                    .fillMaxHeight()
            )
            if (showBins && showTurns) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_active_bins),
                        value = state.activeBins.toString(),
                        accent = SproutGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_turns_month),
                        value = state.turnsThisMonth.toString(),
                        accent = Terracotta,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            } else if (showBins) {
                StatCard(
                    label = stringResource(R.string.stat_active_bins),
                    value = state.activeBins.toString(),
                    accent = SproutGreen,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OrganicCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
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
                        text = verdictLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = verdictColor,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
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
                    greenRatio = state.greenRatio,
                    description = stringResource(
                        R.string.cd_balance,
                        greenPercent,
                        100 - greenPercent
                    ),
                    barHeight = 18,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (state.summaries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                OrganicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.sprite_5),
                            contentDescription = stringResource(R.string.cd_seedling),
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.empty_bins_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Parchment,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.empty_bins_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoilMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(items = state.summaries, key = { it.bin.id }) { summary ->
                    BinCard(summary = summary, onOpen = { onOpenBin(summary.bin.id) })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.cta_materials),
                onClick = onMaterials,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.cta_batches),
                onClick = onBatches,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.cta_notes),
                onClick = onNotes,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.showAddBin) {
        AddBinDialog(onConfirm = onConfirmAddBin, onDismiss = onDismissAddBin)
    }
}

@Composable
private fun StartTile(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = OrganicShape,
        elevation = null,
        contentPadding = PaddingValues(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Parchment
        ),
        modifier = modifier
            .shadow(8.dp, OrganicShape)
            .clip(OrganicShape)
            .background(SoilSurface)
            .background(
                Brush.linearGradient(
                    listOf(
                        SproutGreen.copy(alpha = 0.26f),
                        SproutDeep.copy(alpha = 0.18f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(R.string.cd_greens),
                modifier = Modifier.size(44.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(R.string.cta_start),
                style = MaterialTheme.typography.titleMedium,
                color = Parchment
            )
        }
    }
}
