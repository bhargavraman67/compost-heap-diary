package com.compostheap.diary.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object BinYard : Screen
    data class AddToBin(val binId: String) : Screen
    data class BinTimeline(val binId: String) : Screen
    data class TemperatureChart(val binId: String) : Screen
    data class HarvestRecord(val binId: String) : Screen
    data object MaterialsList : Screen
    data object PastBatches : Screen
    data object GardenNotes : Screen
    data object Settings : Screen
}
