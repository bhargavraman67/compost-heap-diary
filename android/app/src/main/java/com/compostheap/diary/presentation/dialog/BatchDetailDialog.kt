package com.compostheap.diary.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.DialogShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.ui.SoilBalanceBar
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.Batch
import com.compostheap.diary.domain.model.VolumeUnit

@Composable
fun BatchDetailDialog(
    batch: Batch,
    volumeUnit: VolumeUnit,
    onDismiss: () -> Unit
) {
    val unitLabel = if (volumeUnit == VolumeUnit.LITRES) {
        stringResource(R.string.unit_short_litres)
    } else {
        stringResource(R.string.unit_short_gallons)
    }
    val greenPercent = Formats.percent(batch.greenRatio)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = SoilSurface,
        titleContentColor = Parchment,
        textContentColor = Parchment,
        title = {
            Text(text = batch.binName, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = Formats.dayMonthYear(batch.startedAtMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = SoilMuted
                    )
                    Text(
                        text = Formats.dayMonthYear(batch.harvestedAtMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentDim
                    )
                }
                Text(
                    text = stringResource(
                        R.string.fmt_volume,
                        Formats.volumeValue(batch.yieldLitres, volumeUnit),
                        unitLabel
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = SproutGreen
                )
                SoilBalanceBar(
                    greenRatio = batch.greenRatio,
                    description = stringResource(
                        R.string.cd_balance,
                        greenPercent,
                        100 - greenPercent
                    ),
                    barHeight = 10,
                    modifier = Modifier.fillMaxWidth()
                )
                if (batch.note.isNotEmpty()) {
                    Text(
                        text = batch.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ParchmentDim
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_close),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
