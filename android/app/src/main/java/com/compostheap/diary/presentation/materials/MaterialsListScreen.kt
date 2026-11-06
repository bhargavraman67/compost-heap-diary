package com.compostheap.diary.presentation.materials

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.BarkDeep
import com.compostheap.diary.core.theme.BarkLight
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilDeep
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.SegmentedTile
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.presentation.dialog.AddMaterialDialog

private val MaterialsOverlay = listOf(
    Color(0xCC2B2118),
    Color(0xE62B2118),
    Color(0xF01C150F)
)

@Composable
fun MaterialsListScreen(onBack: () -> Unit) {
    val viewModel: MaterialsListViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MaterialsListContent(
        state = state,
        onBack = onBack,
        onSelectType = viewModel::selectType,
        onAdd = viewModel::openAddDialog,
        onDismissAdd = viewModel::dismissAddDialog,
        onConfirmAdd = viewModel::confirmAdd,
        onDelete = viewModel::delete
    )
}

@Composable
private fun MaterialsListContent(
    state: MaterialsListUiState,
    onBack: () -> Unit,
    onSelectType: (MaterialType) -> Unit,
    onAdd: () -> Unit,
    onDismissAdd: () -> Unit,
    onConfirmAdd: (String, MaterialType) -> Unit,
    onDelete: (String) -> Unit
) {
    ScreenScaffold(
        overlay = MaterialsOverlay,
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
                    contentDescription = stringResource(R.string.cd_add_material),
                    modifier = Modifier.size(28.dp),
                    tint = SoilDeep
                )
            }
        }
    ) {
        ScreenHeader(
            title = stringResource(R.string.materials_title),
            subtitle = stringResource(R.string.yard_subtitle),
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SegmentedTile(
                label = stringResource(R.string.label_greens),
                selected = state.selectedType == MaterialType.GREEN,
                gradient = listOf(SproutGreen, SproutDeep),
                onClick = { onSelectType(MaterialType.GREEN) },
                iconPainter = painterResource(R.drawable.sprite_1)
            )
            SegmentedTile(
                label = stringResource(R.string.label_browns),
                selected = state.selectedType == MaterialType.BROWN,
                gradient = listOf(BarkLight, BarkDeep),
                onClick = { onSelectType(MaterialType.BROWN) },
                iconPainter = painterResource(R.drawable.sprite_2)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp)
        ) {
            items(items = state.visible, key = { it.id }) { material ->
                OrganicCard(modifier = Modifier.fillMaxWidth(), elevation = 3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                if (material.type == MaterialType.GREEN) {
                                    R.drawable.sprite_1
                                } else {
                                    R.drawable.sprite_2
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = material.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = Parchment,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (material.type == MaterialType.GREEN) {
                                    stringResource(R.string.hint_high_nitrogen)
                                } else {
                                    stringResource(R.string.hint_high_carbon)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = SoilMuted
                            )
                        }
                        if (material.custom) {
                            IconButton(
                                onClick = { onDelete(material.id) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete),
                                    contentDescription = stringResource(R.string.cd_more),
                                    tint = Terracotta,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showAddDialog) {
        AddMaterialDialog(
            initialType = state.selectedType,
            onConfirm = onConfirmAdd,
            onDismiss = onDismissAdd
        )
    }
}
