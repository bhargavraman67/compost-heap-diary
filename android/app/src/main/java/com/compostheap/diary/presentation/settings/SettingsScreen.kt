package com.compostheap.diary.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.BarkDeep
import com.compostheap.diary.core.theme.BarkLight
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilHumus
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SoilRaised
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.SecondaryButton
import com.compostheap.diary.core.ui.SegmentedTile
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.domain.model.ThemeVariant
import com.compostheap.diary.domain.model.VolumeUnit
import com.compostheap.diary.presentation.dialog.ExportDialog
import com.compostheap.diary.presentation.dialog.ImportDialog

private val SettingsOverlay = listOf(
    Color(0xE62B2118),
    Color(0xEE2B2118),
    Color(0xF21C150F)
)

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onBack = onBack,
        onVolumeUnit = viewModel::setVolumeUnit,
        onTempUnit = viewModel::setTempUnit,
        onTheme = viewModel::setTheme,
        onExport = viewModel::openExport,
        onDismissExport = viewModel::dismissExport,
        onImport = viewModel::openImport,
        onDismissImport = viewModel::dismissImport,
        onConfirmImport = viewModel::confirmImport
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onVolumeUnit: (VolumeUnit) -> Unit,
    onTempUnit: (TempUnit) -> Unit,
    onTheme: (ThemeVariant) -> Unit,
    onExport: () -> Unit,
    onDismissExport: () -> Unit,
    onImport: () -> Unit,
    onDismissImport: () -> Unit,
    onConfirmImport: (String) -> Unit
) {
    ScreenScaffold(overlay = SettingsOverlay) {
        ScreenHeader(
            title = stringResource(R.string.settings_title),
            subtitle = stringResource(R.string.about_line),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(14.dp))

        Section(title = stringResource(R.string.section_units)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SegmentedTile(
                    label = stringResource(R.string.unit_litres),
                    selected = state.volumeUnit == VolumeUnit.LITRES,
                    gradient = listOf(SproutGreen, SproutDeep),
                    onClick = { onVolumeUnit(VolumeUnit.LITRES) },
                    tileHeight = 52
                )
                SegmentedTile(
                    label = stringResource(R.string.unit_gallons),
                    selected = state.volumeUnit == VolumeUnit.GALLONS,
                    gradient = listOf(SproutGreen, SproutDeep),
                    onClick = { onVolumeUnit(VolumeUnit.GALLONS) },
                    tileHeight = 52
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SegmentedTile(
                    label = stringResource(R.string.unit_celsius),
                    selected = state.tempUnit == TempUnit.CELSIUS,
                    gradient = listOf(BarkLight, BarkDeep),
                    onClick = { onTempUnit(TempUnit.CELSIUS) },
                    tileHeight = 52
                )
                SegmentedTile(
                    label = stringResource(R.string.unit_fahrenheit),
                    selected = state.tempUnit == TempUnit.FAHRENHEIT,
                    gradient = listOf(BarkLight, BarkDeep),
                    onClick = { onTempUnit(TempUnit.FAHRENHEIT) },
                    tileHeight = 52
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Section(title = stringResource(R.string.section_theme)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SegmentedTile(
                    label = stringResource(R.string.theme_soil_dark),
                    selected = state.theme == ThemeVariant.SOIL_DARK,
                    gradient = listOf(SoilRaised, BarkDeep),
                    onClick = { onTheme(ThemeVariant.SOIL_DARK) },
                    tileHeight = 52
                )
                SegmentedTile(
                    label = stringResource(R.string.theme_humus_deep),
                    selected = state.theme == ThemeVariant.HUMUS_DEEP,
                    gradient = listOf(SoilHumus, BarkDeep),
                    onClick = { onTheme(ThemeVariant.HUMUS_DEEP) },
                    tileHeight = 52
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Section(title = stringResource(R.string.section_data)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cta_export),
                    onClick = onExport,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_import),
                    onClick = onImport,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OrganicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = Parchment
                    )
                    Text(
                        text = stringResource(R.string.about_version),
                        style = MaterialTheme.typography.labelSmall,
                        color = SoilMuted
                    )
                }
                Text(
                    text = stringResource(R.string.section_about),
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentDim
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.showExport) {
        ExportDialog(snapshot = state.exportText, onDismiss = onDismissExport)
    }

    if (state.showImport) {
        ImportDialog(
            failed = state.importFailed,
            onConfirm = onConfirmImport,
            onDismiss = onDismissImport
        )
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    OrganicCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = ParchmentDim
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
