package shub39.momentum.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single
import shub39.momentum.presentation.home.HomeState
import shub39.momentum.presentation.onboarding.OnboardingState
import shub39.momentum.presentation.project.ProjectState
import shub39.momentum.presentation.settings.SettingsState

@Single
class StateLayer {
    val projectState: MutableStateFlow<ProjectState> = MutableStateFlow(ProjectState())
    val settingsState = MutableStateFlow(SettingsState())
    val onboardingState = MutableStateFlow(OnboardingState())
    val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
}