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
import shub39.momentum.core.domain.interfaces.MontageMaker
import shub39.momentum.core.domain.interfaces.MontageResult
import shub39.momentum.core.domain.interfaces.ProjectRepository
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists

class ProjectViewModel(
    stateLayer: StateLayer,
    private val montageMaker: MontageMaker,
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

            is ProjectAction.OnDeleteDay -> repository.deleteDay(action.day)

            is ProjectAction.OnUpsertDay -> repository.upsertDay(action.day)

            ProjectAction.OnCreateMontage -> {
                Log.d("ProjectViewModel", "Starting montage creation")
                val file = createTempFile(suffix = ".mp4")

                when (
                    val result = montageMaker.createMontage(
                        days = _state.value.days,
                        file = file.toFile()
                    )
                ) {
                    is MontageResult.Error -> {
                        _state.update { it.copy(montage = result) }
                        file.deleteIfExists()
                    }

                    is MontageResult.Success -> {
                        _state.update { it.copy(montage = result) }
                    }
                }
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
                            days = days
                        )
                    }
                }
            }
            .launchIn(this)
    }
}