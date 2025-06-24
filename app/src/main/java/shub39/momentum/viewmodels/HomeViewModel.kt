package shub39.momentum.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.interfaces.ProjectRepository
import shub39.momentum.core.domain.interfaces.SettingsPrefs
import shub39.momentum.core.domain.observePreferenceFlow
import shub39.momentum.home.HomeAction
import shub39.momentum.home.HomeState
import java.time.LocalDate
import java.time.ZoneOffset

class HomeViewModel(
    private val stateLayer: StateLayer,
    private val settingsPrefs: SettingsPrefs,
    private val projectRepository: ProjectRepository
) : ViewModel() {
    private val _state = stateLayer.homeState
    val state = _state.asStateFlow()
        .onStart { observeProjects() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState()
        )

    fun onAction(action: HomeAction) = viewModelScope.launch {
        when (action) {
            is HomeAction.OnChangeNotificationPref -> settingsPrefs.updateNotificationPref(action.pref)

            is HomeAction.OnChangeProject -> stateLayer.projectState.update { it.copy(project = action.project) }

            is HomeAction.OnAddProject -> {
                val startDate = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC)

                projectRepository.upsertProject(
                    Project(
                        title = action.title,
                        description = action.description,
                        startDate = startDate,
                        lastUpdatedDate = startDate
                    )
                )
            }
        }
    }

    private fun observeProjects() = viewModelScope.launch {
        observePreferenceFlow(
            flow = settingsPrefs.getNotificationPrefFlow(),
            scope = this,
            state = _state,
            update = { state, pref -> state.copy(sendNotifications = pref) }
        )

        projectRepository
            .getProjects()
            .onStart { _state.update { it.copy(isLoading = false) } }
            .onEach { projects -> _state.update { it.copy(projects = projects) } }
            .launchIn(this)
    }
}