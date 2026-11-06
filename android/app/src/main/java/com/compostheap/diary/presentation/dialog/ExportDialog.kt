package com.compostheap.diary.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.DialogShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SoilSurface

@Composable
fun ExportDialog(
    snapshot: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = SoilSurface,
        titleContentColor = Parchment,
        textContentColor = Parchment,
        title = {
            Text(
                text = stringResource(R.string.dlg_export_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.dlg_export_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = SoilMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = snapshot,
                    onValueChange = {},
                    readOnly = true,
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
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
