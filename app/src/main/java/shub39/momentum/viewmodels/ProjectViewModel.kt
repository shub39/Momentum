package shub39.momentum.viewmodels

import android.util.Log
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
import shub39.momentum.core.domain.interfaces.ProjectRepository
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import java.time.LocalDate

class ProjectViewModel(
    private val stateLayer: StateLayer,
    private val repository: ProjectRepository
) : ViewModel() {
    private val _state = stateLayer.projectState
    val state = _state.asStateFlow()
        .onStart { observeDays() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjectState()
        )

    fun onAction(action: ProjectAction) = viewModelScope.launch {
        when (action) {
            is ProjectAction.OnUpdateProject -> repository.upsertProject(action.project)

            is ProjectAction.OnDeleteProject -> repository.deleteProject(action.project)

            is ProjectAction.OnDeleteDay -> repository.upsertDay(action.day)

            is ProjectAction.OnUpsertDay -> repository.upsertDay(action.day).also {
                Log.d("ProjectViewModel", "Upserted day: ${action.day.image}")
            }

            ProjectAction.OnSetMontage -> {
                //TODO: Set montage to video page
            }
        }
    }

    fun observeDays() = viewModelScope.launch {
        repository
            .getDays()
            .onEach { days ->
                if (_state.value.project != null) {
                    val days = days.filter { day -> day.projectId == _state.value.project!!.id }

                    _state.update {
                        it.copy(
                            days = days,
                            dates = days.map { day -> LocalDate.ofEpochDay(day.date) }
                        )
                    }
                }
            }
            .launchIn(this)
    }
}