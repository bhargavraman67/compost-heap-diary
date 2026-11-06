package com.compostheap.diary.presentation.menu

import com.compostheap.diary.domain.model.BalanceVerdict
import com.compostheap.diary.domain.model.BinSummary

data class BinYardUiState(
    val summaries: List<BinSummary> = emptyList(),
    val greenRatio: Float = 0.5f,
    val verdict: BalanceVerdict = BalanceVerdict.BALANCED,
    val activeBins: Int = 0,
    val turnsThisMonth: Int = 0,
    val showAddBin: Boolean = false,
    val loaded: Boolean = false
)
