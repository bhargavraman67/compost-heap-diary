package com.compostheap.diary.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.BarkDeep
import com.compostheap.diary.core.theme.BarkLight
import com.compostheap.diary.core.theme.DialogShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.ui.SegmentedTile
import com.compostheap.diary.domain.model.MaterialType

@Composable
fun AddMaterialDialog(
    initialType: MaterialType,
    onConfirm: (String, MaterialType) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(initialType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = SoilSurface,
        titleContentColor = Parchment,
        textContentColor = Parchment,
        title = {
            Text(
                text = stringResource(R.string.dlg_add_material_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { value -> name = value },
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.dlg_material_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SegmentedTile(
                        label = stringResource(R.string.label_greens),
                        selected = type == MaterialType.GREEN,
                        gradient = listOf(SproutGreen, SproutDeep),
                        onClick = { type = MaterialType.GREEN },
                        tileHeight = 52
                    )
                    SegmentedTile(
                        label = stringResource(R.string.label_browns),
                        selected = type == MaterialType.BROWN,
                        gradient = listOf(BarkLight, BarkDeep),
                        onClick = { type = MaterialType.BROWN },
                        tileHeight = 52
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, type) }, enabled = name.isNotBlank()) {
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
