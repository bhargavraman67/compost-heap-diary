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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.DialogShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.Terracotta

@Composable
fun ImportDialog(
    failed: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var raw by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = SoilSurface,
        titleContentColor = Parchment,
        textContentColor = Parchment,
        title = {
            Text(
                text = stringResource(R.string.dlg_import_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.dlg_import_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = SoilMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = raw,
                    onValueChange = { value -> raw = value },
                    minLines = 4,
                    maxLines = 6,
                    isError = failed,
                    modifier = Modifier.fillMaxWidth()
                )
                if (failed) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.error_import),
                        style = MaterialTheme.typography.labelSmall,
                        color = Terracotta
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(raw) }, enabled = raw.isNotBlank()) {
                Text(
                    text = stringResource(R.string.cta_import),
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
