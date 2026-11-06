package com.compostheap.diary.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SoilColorScheme = darkColorScheme(
    primary = SproutGreen,
    onPrimary = SoilDeep,
    primaryContainer = SproutDeep,
    onPrimaryContainer = Parchment,
    secondary = Terracotta,
    onSecondary = SoilDeep,
    secondaryContainer = SoilRaised,
    onSecondaryContainer = Parchment,
    tertiary = EmberGold,
    onTertiary = SoilDeep,
    background = SoilBase,
    onBackground = Parchment,
    surface = SoilSurface,
    onSurface = Parchment,
    surfaceVariant = SoilRaised,
    onSurfaceVariant = ParchmentDim,
    outline = SoilEdge,
    outlineVariant = SoilRaised,
    error = Terracotta,
    onError = SoilDeep,
    scrim = SoilDeep
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SoilColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
