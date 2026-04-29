package com.example.mob_dev_portfolio.features.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.entity.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

//class NavigationViewModel : ViewModel() {
//    private val _currentScreen = MutableStateFlow(AppScreen.Dashboard)
//    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()
//
//    private val _screenStack = MutableStateFlow(listOf<AppScreen>())
//
//    fun navigateTo(screen: AppScreen) {
//        _screenStack.value += _currentScreen.value
//        _currentScreen.value = screen
//    }
//
//    fun navigateBack() {
//        val stack = _screenStack.value
//        _currentScreen.value = if (stack.isNotEmpty()) stack.last() else AppScreen.Dashboard
//        _screenStack.value = if (stack.isNotEmpty()) stack.dropLast(1) else stack
//    }
//
//    fun navigateTo(screen: AppScreen, clearStack: Boolean) {
//        if (clearStack) _screenStack.value = emptyList()
//        _currentScreen.value = screen
//    }
//}

class NavigationViewModel : ViewModel() {
    private val _currentScreen = MutableStateFlow(AppScreen.Dashboard)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenStack = MutableStateFlow(listOf<AppScreen>())
    val canGoBack: StateFlow<Boolean> = _screenStack
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun navigateTo(screen: AppScreen) {
        _screenStack.value += _currentScreen.value
        _currentScreen.value = screen
    }

    fun navigateBack() {
        val stack = _screenStack.value
        _currentScreen.value = if (stack.isNotEmpty()) stack.last() else AppScreen.Dashboard
        _screenStack.value = if (stack.isNotEmpty()) stack.dropLast(1) else stack
    }

    fun navigateTo(screen: AppScreen, clearStack: Boolean) {
        if (clearStack) {
            _screenStack.value = listOf(AppScreen.Dashboard)
        }
        _currentScreen.value = screen
    }
}