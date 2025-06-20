package shub39.momentum.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import shub39.momentum.home.HomeAction
import shub39.momentum.home.HomeState

class HomeViewmodel(
    private val sateLayer: StateLayer
) : ViewModel() {
    private val _state = sateLayer.homeState
    val state = _state.asStateFlow()
        .onStart { }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState()
        )

    fun onAction(action: HomeAction) = viewModelScope.launch {
        when (action) {
            else -> {}
        }
    }
}