package com.compostheap.diary.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compostheap.diary.core.config.GameConfig
import com.compostheap.diary.core.di.AppViewModelFactory
import com.compostheap.diary.core.navigation.Screen
import com.compostheap.diary.presentation.archive.PastBatchesScreen
import com.compostheap.diary.presentation.game.AddToBinScreen
import com.compostheap.diary.presentation.gameover.HarvestResultScreen
import com.compostheap.diary.presentation.materials.MaterialsListScreen
import com.compostheap.diary.presentation.menu.BinYardScreen
import com.compostheap.diary.presentation.notes.GardenNotesScreen
import com.compostheap.diary.presentation.settings.SettingsScreen
import com.compostheap.diary.presentation.splash.SplashScreen
import com.compostheap.diary.presentation.temperature.TemperatureChartScreen
import com.compostheap.diary.presentation.timeline.BinTimelineScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            if (initialState == Screen.Splash) {
                fadeIn(animationSpec = tween(GameConfig.SPLASH_FADE_IN_MS)) togetherWith
                    fadeOut(animationSpec = tween(GameConfig.SPLASH_FADE_OUT_MS))
            } else {
                (slideInHorizontally(
                    animationSpec = tween(GameConfig.NAV_IN_MS, easing = FastOutSlowInEasing)
                ) { full -> full / 6 } + fadeIn(
                    animationSpec = tween(GameConfig.NAV_IN_MS)
                )) togetherWith fadeOut(animationSpec = tween(GameConfig.NAV_OUT_MS))
            }
        },
        label = "screen"
    ) { target ->
        when (target) {
            Screen.Splash -> SplashScreen(onFinished = { appViewModel.openYard() })

            Screen.BinYard -> BinYardScreen(
                onStartLogging = { binId -> appViewModel.navigate(Screen.AddToBin(binId)) },
                onOpenBin = { binId -> appViewModel.navigate(Screen.BinTimeline(binId)) },
                onMaterials = { appViewModel.navigate(Screen.MaterialsList) },
                onBatches = { appViewModel.navigate(Screen.PastBatches) },
                onNotes = { appViewModel.navigate(Screen.GardenNotes) },
                onSettings = { appViewModel.navigate(Screen.Settings) }
            )

            is Screen.AddToBin -> AddToBinScreen(
                binId = target.binId,
                onBack = { appViewModel.back() },
                onTimeline = { binId -> appViewModel.navigate(Screen.BinTimeline(binId)) },
                onHarvest = { binId -> appViewModel.openHarvest(binId) }
            )

            is Screen.BinTimeline -> BinTimelineScreen(
                binId = target.binId,
                onBack = { appViewModel.back() },
                onTemperature = { binId -> appViewModel.navigate(Screen.TemperatureChart(binId)) },
                onHarvest = { binId -> appViewModel.openHarvest(binId) }
            )

            is Screen.TemperatureChart -> TemperatureChartScreen(
                binId = target.binId,
                onBack = { appViewModel.back() }
            )

            is Screen.HarvestRecord -> HarvestResultScreen(
                binId = target.binId,
                onPlayAgain = { binId -> appViewModel.restartBatch(binId) },
                onMenu = { appViewModel.openYard() }
            )

            Screen.MaterialsList -> MaterialsListScreen(onBack = { appViewModel.back() })

            Screen.PastBatches -> PastBatchesScreen(onBack = { appViewModel.back() })

            Screen.GardenNotes -> GardenNotesScreen(onBack = { appViewModel.back() })

            Screen.Settings -> SettingsScreen(onBack = { appViewModel.back() })
        }
    }
}
