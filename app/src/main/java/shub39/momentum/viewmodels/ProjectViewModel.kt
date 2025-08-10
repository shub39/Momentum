package shub39.momentum.viewmodels

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlayer.Builder
import androidx.media3.exoplayer.ExoPlayer.REPEAT_MODE_ALL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.core.domain.enums.VideoAction
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

    private val _exoPlayer = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayer = _exoPlayer.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _state = stateLayer.projectState
    val state = _state.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjectState()
        )

    fun onAction(action: ProjectAction) {
        when (action) {
            is ProjectAction.OnUpdateProject -> viewModelScope.launch {
                repository.upsertProject(action.project)
                _state.update { it.copy(project = action.project) }
            }

            is ProjectAction.OnDeleteProject -> viewModelScope.launch {
                repository.deleteProject(action.project)
            }

            is ProjectAction.OnDeleteDay -> viewModelScope.launch {
                repository.deleteDay(action.day)
            }

            is ProjectAction.OnUpsertDay -> viewModelScope.launch {
                repository.upsertDay(action.day)
            }

            is ProjectAction.OnCreateMontage -> viewModelScope.launch {
                if (_exoPlayer.value == null) {
                    _exoPlayer.update {
                        Builder(action.context)
                            .build()
                            .apply {
                                prepare()
                                repeatMode = REPEAT_MODE_ALL
                            }
                    }
                }

                _state.update { it.copy(montage = MontageState.Processing) }

                val file = createTempFile(suffix = ".mp4")

                Log.d("ProjectViewModel", "Starting montage creation")

                when (val result = montageMaker.createMontage(
                    days = action.days,
                    file = file.toFile(),
                    montageConfig = _state.value.montageConfig
                )) {
                    is MontageState.Success -> {
                        _exoPlayer.value?.apply {
                            clearMediaItems()
                            setMediaItem(MediaItem.fromUri(result.file.toUri()))
                            prepare()
                        }

                        _state.update { it.copy(montage = result) }
                    }

                    else -> _state.update { it.copy(montage = result) }
                }
            }

            ProjectAction.OnUpdateDays -> refreshDays()

            is ProjectAction.OnUpdateSelectedDay -> _state.update { it.copy(selectedDate = action.day) }

            ProjectAction.OnClearMontageState -> {
                _exoPlayer.value?.release()
                _exoPlayer.update { null }
                _state.update { it.copy(montage = MontageState.Processing) }
            }

            is ProjectAction.OnPlayerAction -> {
                _exoPlayer.value?.let {
                    when (action.playerAction.action) {
                        VideoAction.PLAY -> it.play()
                        VideoAction.PAUSE -> it.pause()
                        VideoAction.SEEK -> {
                            (action.playerAction.data as? Long)?.let { position ->
                                it.seekTo(position)
                            }
                        }
                    }
                }
            }
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

    override fun onCleared() {
        super.onCleared()
        _exoPlayer.value?.release()
    }
}