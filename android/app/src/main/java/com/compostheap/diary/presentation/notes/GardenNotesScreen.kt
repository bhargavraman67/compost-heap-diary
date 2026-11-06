package com.compostheap.diary.presentation.notes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilDeep
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.presentation.dialog.AddNoteDialog

private val NotesOverlay = listOf(
    Color(0xCC2B2118),
    Color(0xE62B2118),
    Color(0xF01C150F)
)

@Composable
fun GardenNotesScreen(onBack: () -> Unit) {
    val viewModel: GardenNotesViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GardenNotesContent(
        state = state,
        onBack = onBack,
        onAdd = viewModel::openAddDialog,
        onDismissAdd = viewModel::dismissAddDialog,
        onConfirmAdd = viewModel::confirmAdd
    )
}

@Composable
private fun GardenNotesContent(
    state: GardenNotesUiState,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onDismissAdd: () -> Unit,
    onConfirmAdd: (String, String, String?) -> Unit
) {
    ScreenScaffold(
        overlay = NotesOverlay,
        floating = {
            IconButton(
                onClick = onAdd,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp)
                    .shadow(8.dp, CircleShape)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(SproutGreen, SproutDeep)))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.cd_add_note),
                    modifier = Modifier.size(28.dp),
                    tint = SoilDeep
                )
            }
        }
    ) {
        ScreenHeader(
            title = stringResource(R.string.notes_title),
            subtitle = stringResource(R.string.harvest_hint),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.notes.isEmpty()) {
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
                        text = stringResource(R.string.empty_notes),
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
                contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp)
            ) {
                items(items = state.notes, key = { it.id }) { note ->
                    OrganicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(56.dp)
                                    .background(SproutGreen)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = note.bedName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Parchment,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = Formats.dayMonthYear(note.createdAtMs),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoilMuted
                                )
                                if (note.text.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = note.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ParchmentDim,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (state.showAddDialog) {
        AddNoteDialog(
            batches = state.batches,
            onConfirm = onConfirmAdd,
            onDismiss = onDismissAdd
        )
    }
}
