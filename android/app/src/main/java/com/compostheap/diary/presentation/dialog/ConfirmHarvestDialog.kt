package com.compostheap.diary.presentation.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.DialogShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.Terracotta

@Composable
fun ConfirmHarvestDialog(
    onConfirm: () -> Unit,
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
                text = stringResource(R.string.dlg_confirm_harvest_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dlg_confirm_harvest_body),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.cta_harvest),
                    style = MaterialTheme.typography.labelLarge,
                    color = Terracotta
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
