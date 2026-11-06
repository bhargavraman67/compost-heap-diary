package com.compostheap.diary.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.compostheap.diary.R
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.PillShape
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.BinSummary

@Composable
fun BinCard(
    summary: BinSummary,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greenPercent = Formats.percent(summary.balance.greenRatio)
    OrganicCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 112.dp)
            .clickable(onClick = onOpen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_2),
                contentDescription = stringResource(R.string.cd_browns),
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = summary.bin.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (summary.bin.resting) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Pill(text = stringResource(R.string.label_resting), tint = ParchmentDim)
                    }
                    if (summary.ready) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Pill(text = stringResource(R.string.label_ready), tint = Terracotta)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fmt_day, summary.dayCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = SoilMuted
                    )
                    Text(
                        text = stringResource(R.string.timeline_last_turn),
                        style = MaterialTheme.typography.labelSmall,
                        color = SoilMuted
                    )
                    Text(
                        text = when {
                            summary.daysSinceTurn == null -> stringResource(R.string.label_never)
                            summary.daysSinceTurn == 0 -> stringResource(R.string.fmt_today)
                            else -> stringResource(R.string.fmt_days_ago, summary.daysSinceTurn)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = ParchmentDim,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                SoilBalanceBar(
                    greenRatio = summary.balance.greenRatio,
                    description = stringResource(
                        R.string.cd_balance,
                        greenPercent,
                        100 - greenPercent
                    ),
                    barHeight = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                MaturityRail(progress = summary.progress)
            }
        }
    }
}

@Composable
private fun MaturityRail(progress: Float) {
    val safe = progress.coerceIn(0.02f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(SoilEdge.copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(safe)
                .height(3.dp)
                .background(SproutGreen)
        )
    }
}

@Composable
private fun Pill(text: String, tint: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(tint.copy(alpha = 0.22f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            maxLines = 1
        )
    }
}
