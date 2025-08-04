package shub39.momentum.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import shub39.momentum.home.HomeState
import shub39.momentum.onboarding.OnboardingState
import shub39.momentum.project.ProjectState
import shub39.momentum.settings.SettingsState

class StateLayer {
    val projectState: MutableStateFlow<ProjectState> = MutableStateFlow(ProjectState.Loading)
    val settingsState = MutableStateFlow(SettingsState())
    val onboardingState = MutableStateFlow(OnboardingState())
    val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Loading)
}