package com.compostheap.diary.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.ChipShape
import com.compostheap.diary.core.theme.DialogShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.domain.model.Batch

@Composable
fun AddNoteDialog(
    batches: List<Batch>,
    onConfirm: (String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var bedName by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var batchId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = SoilSurface,
        titleContentColor = Parchment,
        textContentColor = Parchment,
        title = {
            Text(
                text = stringResource(R.string.dlg_add_note_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = bedName,
                    onValueChange = { value -> bedName = value },
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.dlg_bed_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { value -> text = value },
                    minLines = 2,
                    maxLines = 3,
                    label = { Text(text = stringResource(R.string.dlg_note_text)) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (batches.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.notes_from_batch),
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentDim
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = batches, key = { it.id }) { batch ->
                            val selected = batch.id == batchId
                            Box(
                                modifier = Modifier
                                    .clip(ChipShape)
                                    .background(
                                        if (selected) {
                                            SproutGreen.copy(alpha = 0.22f)
                                        } else {
                                            SoilSurface
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) SproutGreen else SoilEdge,
                                        ChipShape
                                    )
                                    .clickable {
                                        batchId = if (selected) null else batch.id
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = batch.binName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) SproutGreen else ParchmentDim,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(bedName, text, batchId) },
                enabled = bedName.isNotBlank()
            ) {
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
