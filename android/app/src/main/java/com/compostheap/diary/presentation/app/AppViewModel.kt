package com.compostheap.diary.presentation.app

import androidx.lifecycle.ViewModel
import com.compostheap.diary.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun openYard() {
        backStack.clear()
        _screen.value = Screen.BinYard
        _canGoBack.value = false
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash && current != target) {
            backStack.addLast(current)
        }
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun openHarvest(binId: String) {
        backStack.clear()
        backStack.addLast(Screen.BinYard)
        _screen.value = Screen.HarvestRecord(binId)
        _canGoBack.value = true
    }

    fun restartBatch(binId: String) {
        backStack.clear()
        backStack.addLast(Screen.BinYard)
        _screen.value = Screen.AddToBin(binId)
        _canGoBack.value = true
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }
}
