package shub39.momentum.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single
import shub39.momentum.home.HomeState
import shub39.momentum.onboarding.OnboardingState
import shub39.momentum.project.ProjectState
import shub39.momentum.settings.SettingsState

@Single
class StateLayer {
    val projectState: MutableStateFlow<ProjectState> = MutableStateFlow(ProjectState())
    val settingsState = MutableStateFlow(SettingsState())
    val onboardingState = MutableStateFlow(OnboardingState())
    val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
}