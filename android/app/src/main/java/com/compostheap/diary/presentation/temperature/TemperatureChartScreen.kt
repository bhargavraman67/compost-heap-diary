package com.compostheap.diary.presentation.temperature

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.R
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.theme.EmberGold
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.ParchmentDim
import com.compostheap.diary.core.theme.SoilEdge
import com.compostheap.diary.core.theme.SoilMuted
import com.compostheap.diary.core.theme.SproutGreen
import com.compostheap.diary.core.theme.Terracotta
import com.compostheap.diary.core.ui.OrganicCard
import com.compostheap.diary.core.ui.PrimaryButton
import com.compostheap.diary.core.ui.ScreenHeader
import com.compostheap.diary.core.ui.ScreenScaffold
import com.compostheap.diary.core.ui.StatCard
import com.compostheap.diary.core.util.Formats
import com.compostheap.diary.domain.model.TempUnit
import com.compostheap.diary.presentation.dialog.TemperatureDialog

private val TemperatureOverlay = listOf(
    Color(0xD92B2118),
    Color(0xE62B2118),
    Color(0xF21C150F)
)

@Composable
fun TemperatureChartScreen(
    binId: String,
    onBack: () -> Unit
) {
    val viewModel: TemperatureChartViewModel = viewModel(
        key = "temperature_$binId",
        factory = remember(binId) { AppViewModelFactory(binId) }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TemperatureChartContent(
        state = state,
        onBack = onBack,
        onOpenDialog = viewModel::openDialog,
        onDismissDialog = viewModel::dismissDialog,
        onConfirm = viewModel::addReading
    )
}

@Composable
private fun TemperatureChartContent(
    state: TemperatureChartUiState,
    onBack: () -> Unit,
    onOpenDialog: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    val peakValue = Formats.temperatureValue(state.peakCelsius, state.tempUnit)
    val averageValue = Formats.temperatureValue(state.averageCelsius, state.tempUnit)
    val chartDescription = stringResource(
        R.string.cd_chart,
        state.readings.size,
        peakValue
    )

    ScreenScaffold(overlay = TemperatureOverlay) {
        ScreenHeader(
            title = stringResource(R.string.temp_title),
            subtitle = state.binName,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.readings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = stringResource(R.string.cd_thermometer),
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.empty_temp),
                        style = MaterialTheme.typography.titleMedium,
                        color = SoilMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            OrganicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .semantics { contentDescription = chartDescription }
                    ) {
                        val chartWidth = size.width
                        val chartHeight = size.height
                        val maxValue = GameConfig.MAX_CELSIUS

                        drawRect(
                            color = Terracotta.copy(alpha = 0.18f),
                            topLeft = Offset(0f, 0f),
                            size = Size(chartWidth, chartHeight * 0.33f)
                        )
                        drawRect(
                            color = SproutGreen.copy(alpha = 0.18f),
                            topLeft = Offset(0f, chartHeight * 0.33f),
                            size = Size(chartWidth, chartHeight * 0.34f)
                        )
                        drawRect(
                            color = SoilEdge.copy(alpha = 0.18f),
                            topLeft = Offset(0f, chartHeight * 0.67f),
                            size = Size(chartWidth, chartHeight * 0.33f)
                        )

                        listOf(20f, 40f, 60f).forEach { mark ->
                            val y = chartHeight * (1f - mark / maxValue)
                            drawLine(
                                color = SoilEdge.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(chartWidth, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val values = state.readings.map { it.celsius }
                        if (values.size >= 2) {
                            val stepX = chartWidth / (values.size - 1)
                            val line = Path()
                            val fill = Path()
                            fill.moveTo(0f, chartHeight)
                            values.forEachIndexed { index, value ->
                                val x = stepX * index
                                val y = chartHeight * (1f - (value / maxValue).coerceIn(0f, 1f))
                                if (index == 0) line.moveTo(x, y) else line.lineTo(x, y)
                                fill.lineTo(x, y)
                            }
                            fill.lineTo(chartWidth, chartHeight)
                            fill.close()
                            drawPath(
                                path = fill,
                                brush = Brush.verticalGradient(
                                    listOf(Terracotta.copy(alpha = 0.28f), Color.Transparent)
                                )
                            )
                            drawPath(
                                path = line,
                                color = Terracotta,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                            values.forEachIndexed { index, value ->
                                val x = stepX * index
                                val y = chartHeight * (1f - (value / maxValue).coerceIn(0f, 1f))
                                drawCircle(
                                    color = Parchment,
                                    radius = 2.5f.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.stat_peak),
                    value = formatTemp(peakValue, state.tempUnit),
                    accent = Terracotta,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.stat_average),
                    value = formatTemp(averageValue, state.tempUnit),
                    accent = EmberGold,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.stat_readings),
                    value = state.readings.size.toString(),
                    accent = ParchmentDim,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(items = state.readings.reversed(), key = { it.id }) { reading ->
                    OrganicCard(modifier = Modifier.fillMaxWidth(), elevation = 3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Formats.dayMonth(reading.timestampMs),
                                style = MaterialTheme.typography.bodyMedium,
                                color = ParchmentDim
                            )
                            Text(
                                text = formatTemp(
                                    Formats.temperatureValue(reading.celsius, state.tempUnit),
                                    state.tempUnit
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                color = EmberGold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        PrimaryButton(
            text = stringResource(R.string.cta_add_reading),
            onClick = onOpenDialog,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.showDialog) {
        TemperatureDialog(
            tempUnit = state.tempUnit,
            onConfirm = onConfirm,
            onDismiss = onDismissDialog
        )
    }
}

@Composable
private fun formatTemp(value: Int, unit: TempUnit): String = if (unit == TempUnit.CELSIUS) {
    stringResource(R.string.fmt_celsius, value)
} else {
    stringResource(R.string.fmt_fahrenheit, value)
}
