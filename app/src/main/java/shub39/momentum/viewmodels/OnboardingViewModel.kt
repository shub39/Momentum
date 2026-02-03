package shub39.momentum.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.domain.interfaces.SettingsPrefs
import shub39.momentum.onboarding.OnboardingAction
import shub39.momentum.onboarding.OnboardingState

@KoinViewModel
class OnboardingViewModel(
    private val stateLayer: StateLayer,
    private val datastore: SettingsPrefs
) : ViewModel() {
    private val _state = stateLayer.onboardingState
    val state = _state.asStateFlow()
        .onStart { }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OnboardingState()
        )

    fun onAction(action: OnboardingAction) = viewModelScope.launch {
        when (action) {
            OnboardingAction.OnOnboardingDone -> datastore.updateOnboardingDone(true)
            is OnboardingAction.OnPermissionChange -> _state.update { it.copy(isPermissionGranted = action.isGranted) }
        }
    }
}