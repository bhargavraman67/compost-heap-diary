package com.compostheap.diary.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.compostheap.diary.presentation.app.AppViewModel
import com.compostheap.diary.presentation.archive.PastBatchesViewModel
import com.compostheap.diary.presentation.game.AddToBinViewModel
import com.compostheap.diary.presentation.gameover.HarvestRecordViewModel
import com.compostheap.diary.presentation.materials.MaterialsListViewModel
import com.compostheap.diary.presentation.menu.BinYardViewModel
import com.compostheap.diary.presentation.notes.GardenNotesViewModel
import com.compostheap.diary.presentation.settings.SettingsViewModel
import com.compostheap.diary.presentation.splash.SplashViewModel
import com.compostheap.diary.presentation.temperature.TemperatureChartViewModel
import com.compostheap.diary.presentation.timeline.BinTimelineViewModel

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(private val binId: String = "") : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel() as T

        modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel() as T

        modelClass.isAssignableFrom(BinYardViewModel::class.java) -> BinYardViewModel(
            ServiceLocator.compostRepository,
            ServiceLocator.settingsRepository,
            ServiceLocator.summariseBin
        ) as T

        modelClass.isAssignableFrom(AddToBinViewModel::class.java) -> AddToBinViewModel(
            binId,
            ServiceLocator.compostRepository,
            ServiceLocator.settingsRepository,
            ServiceLocator.summariseBin,
            ServiceLocator.computeBalance,
            ServiceLocator.logEntry
        ) as T

        modelClass.isAssignableFrom(HarvestRecordViewModel::class.java) -> HarvestRecordViewModel(
            binId,
            ServiceLocator.compostRepository,
            ServiceLocator.settingsRepository,
            ServiceLocator.summariseBin,
            ServiceLocator.harvestBatch
        ) as T

        modelClass.isAssignableFrom(BinTimelineViewModel::class.java) -> BinTimelineViewModel(
            binId,
            ServiceLocator.compostRepository,
            ServiceLocator.settingsRepository,
            ServiceLocator.summariseBin,
            ServiceLocator.buildTimeline
        ) as T

        modelClass.isAssignableFrom(TemperatureChartViewModel::class.java) ->
            TemperatureChartViewModel(
                binId,
                ServiceLocator.compostRepository,
                ServiceLocator.settingsRepository
            ) as T

        modelClass.isAssignableFrom(MaterialsListViewModel::class.java) -> MaterialsListViewModel(
            ServiceLocator.compostRepository
        ) as T

        modelClass.isAssignableFrom(PastBatchesViewModel::class.java) -> PastBatchesViewModel(
            ServiceLocator.compostRepository,
            ServiceLocator.settingsRepository
        ) as T

        modelClass.isAssignableFrom(GardenNotesViewModel::class.java) -> GardenNotesViewModel(
            ServiceLocator.compostRepository
        ) as T

        modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(
            ServiceLocator.settingsRepository,
            ServiceLocator.exportDiary,
            ServiceLocator.importDiary
        ) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: " + modelClass.name)
    }
}
