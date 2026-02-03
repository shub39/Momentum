package shub39.momentum.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single
import shub39.momentum.presentation.home.HomeState
import shub39.momentum.presentation.project.ProjectState

@Single
class SharedState {
    val projectState: MutableStateFlow<ProjectState> = MutableStateFlow(ProjectState())
    val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
}