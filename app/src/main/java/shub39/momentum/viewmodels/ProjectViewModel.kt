package shub39.momentum.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.core.domain.interfaces.MontageMaker
import shub39.momentum.core.domain.interfaces.MontageState
import shub39.momentum.core.domain.interfaces.ProjectRepository
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import kotlin.io.path.createTempFile

@KoinViewModel
class ProjectViewModel(
    stateLayer: StateLayer,
    private val montageMaker: MontageMaker,
    private val repository: ProjectRepository
) : ViewModel() {
    private var observeDaysJob: Job? = null

    private val _state = stateLayer.projectState
    val state = _state.asStateFlow()
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

            is ProjectAction.OnCreateMontage -> {
                _state.update {
                    it.copy(montage = MontageState.Processing)
                }

                val file = createTempFile(suffix = ".mp4")

                Log.d("ProjectViewModel", "Starting montage creation")

                val result = montageMaker.createMontage(
                    days = action.days,
                    file = file.toFile(),
                    montageConfig = _state.value.montageConfig
                )

                _state.update {
                    it.copy(montage = result)
                }
            }

            ProjectAction.OnUpdateDays -> refreshDays()

            is ProjectAction.OnUpdateSelectedDay -> _state.update { it.copy(selectedDate = action.day) }
        }
    }

    private fun refreshDays() {
        observeDaysJob?.cancel()
        observeDaysJob = viewModelScope.launch {
            repository.getDays()
                .onEach { days ->
                    val filteredDays = async(Dispatchers.Default) {
                        days.filter { it.projectId == _state.value.project?.id }
                            .sortedByDescending { it.date }
                    }
                    _state.update {
                        it.copy(
                            days = filteredDays.await()
                        )
                    }
                }
                .launchIn(this)
        }
    }
}