package com.compostheap.diary.core.di

import android.content.Context
import com.compostheap.diary.data.local.CompostPreferences
import com.compostheap.diary.data.local.SettingsPreferences
import com.compostheap.diary.data.repository.CompostRepositoryImpl
import com.compostheap.diary.data.repository.SettingsRepositoryImpl
import com.compostheap.diary.domain.repository.CompostRepository
import com.compostheap.diary.domain.repository.SettingsRepository
import com.compostheap.diary.domain.usecase.BuildTimelineUseCase
import com.compostheap.diary.domain.usecase.ComputeBalanceUseCase
import com.compostheap.diary.domain.usecase.ExportDiaryUseCase
import com.compostheap.diary.domain.usecase.HarvestBatchUseCase
import com.compostheap.diary.domain.usecase.ImportDiaryUseCase
import com.compostheap.diary.domain.usecase.LogEntryUseCase
import com.compostheap.diary.domain.usecase.SummariseBinUseCase

object ServiceLocator {

    private var compost: CompostRepository? = null
    private var settings: SettingsRepository? = null

    fun init(context: Context) {
        if (compost != null) return
        compost = CompostRepositoryImpl(CompostPreferences(context.applicationContext))
        settings = SettingsRepositoryImpl(SettingsPreferences(context.applicationContext))
    }

    val compostRepository: CompostRepository get() = checkNotNull(compost)

    val settingsRepository: SettingsRepository get() = checkNotNull(settings)

    val computeBalance: ComputeBalanceUseCase = ComputeBalanceUseCase()

    val summariseBin: SummariseBinUseCase = SummariseBinUseCase(computeBalance)

    val buildTimeline: BuildTimelineUseCase = BuildTimelineUseCase()

    val logEntry: LogEntryUseCase get() = LogEntryUseCase(compostRepository)

    val harvestBatch: HarvestBatchUseCase get() = HarvestBatchUseCase(compostRepository, computeBalance)

    val exportDiary: ExportDiaryUseCase get() = ExportDiaryUseCase(compostRepository)

    val importDiary: ImportDiaryUseCase get() = ImportDiaryUseCase(compostRepository)
}
