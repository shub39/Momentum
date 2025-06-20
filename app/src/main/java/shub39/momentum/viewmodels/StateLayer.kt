package shub39.momentum.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import shub39.momentum.home.HomeState
import shub39.momentum.onboarding.OnboardingState
import shub39.momentum.settings.SettingsState

class StateLayer {
    val settingsState = MutableStateFlow(SettingsState())
    val onboardingState = MutableStateFlow(OnboardingState())
    val homeState = MutableStateFlow(HomeState())
}