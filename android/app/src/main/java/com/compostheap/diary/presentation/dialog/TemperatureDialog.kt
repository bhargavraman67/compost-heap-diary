package com.compostheap.diary.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.theme.DialogShape
import com.compostheap.diary.core.theme.EmberGold
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.TempUnit

@Composable
fun TemperatureDialog(
    tempUnit: TempUnit,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var celsius by remember { mutableFloatStateOf(GameConfig.DEFAULT_CELSIUS) }
    val display = Formats.temperatureValue(celsius, tempUnit)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = SoilSurface,
        titleContentColor = Parchment,
        textContentColor = Parchment,
        title = {
            Text(
                text = stringResource(R.string.dlg_temperature_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = stringResource(R.string.cd_thermometer),
                    modifier = Modifier.size(56.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (tempUnit == TempUnit.CELSIUS) {
                        stringResource(R.string.fmt_celsius, display)
                    } else {
                        stringResource(R.string.fmt_fahrenheit, display)
                    },
                    style = MaterialTheme.typography.displaySmall,
                    color = EmberGold,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = celsius,
                    onValueChange = { value -> celsius = value },
                    valueRange = GameConfig.MIN_CELSIUS..GameConfig.MAX_CELSIUS,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(celsius) }) {
                Text(
                    text = stringResource(R.string.cta_save),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_cancel),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
