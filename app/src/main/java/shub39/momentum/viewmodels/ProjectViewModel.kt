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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.core.domain.enums.VideoAction
import shub39.momentum.core.domain.interfaces.AlarmScheduler
import shub39.momentum.core.domain.interfaces.FaceDetector
import shub39.momentum.core.domain.interfaces.MontageConfigPrefs
import shub39.momentum.core.domain.interfaces.MontageMaker
import shub39.momentum.core.domain.interfaces.MontageState
import shub39.momentum.core.domain.interfaces.ProjectRepository
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import kotlin.io.path.createTempFile

@KoinViewModel
class ProjectViewModel(
    private val stateLayer: StateLayer,
    private val montageMaker: MontageMaker,
    private val faceDetector: FaceDetector,
    private val repository: ProjectRepository,
    private val montageConfigPrefs: MontageConfigPrefs,
    private val scheduler: AlarmScheduler
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
        .onStart { observeConfig() }
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
                scheduler.schedule(action.project)
            }

            is ProjectAction.OnDeleteProject -> viewModelScope.launch {
                repository.deleteProject(action.project)
                scheduler.cancel(action.project)
            }

            is ProjectAction.OnDeleteDay -> viewModelScope.launch {
                repository.deleteDay(action.day)
            }

            is ProjectAction.OnUpsertDay -> viewModelScope.launch {
                val faceData = faceDetector.getFaceDataFromUri(action.day.image.toUri())
                repository.upsertDay(action.day.copy(faceData = faceData))
            }

            is ProjectAction.OnCreateMontage -> viewModelScope.launch {
                montageMaker.createMontageFlow(
                    days = action.days,
                    file = createTempFile(suffix = ".mp4").toFile(),
                    config = _state.value.montageConfig
                )
                    .flowOn(Dispatchers.Default)
                    .collect { state ->
                        Log.d("ProjectViewModel", "Montage state: $state")

                        if (state is MontageState.Success) {
                            _exoPlayer.value?.apply {
                                clearMediaItems()
                                setMediaItem(MediaItem.fromUri(state.file.toUri()))
                                prepare()
                            }
                        }

                        _state.update { it.copy(montage = state) }
                    }
            }

            ProjectAction.OnUpdateDays -> viewModelScope.launch {
                refreshDays()
                processDays()
            }

            is ProjectAction.OnUpdateSelectedDay -> _state.update { it.copy(selectedDate = action.day) }

            ProjectAction.OnClearMontageState -> {
                _exoPlayer.value?.release()
                _exoPlayer.update { null }
                _state.update { it.copy(montage = MontageState.Processing()) }
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

            is ProjectAction.OnInitializeExoPlayer -> {
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
            }

            is ProjectAction.OnEditMontageConfig -> {
                viewModelScope.launch {
                    montageConfigPrefs.setFpi(action.config.framesPerImage)
                    montageConfigPrefs.setFps(action.config.framesPerSecond)
                    montageConfigPrefs.setShowDate(action.config.showDate)
                    montageConfigPrefs.setShowMessage(action.config.showMessage)
                    montageConfigPrefs.setFont(action.config.font)
                    montageConfigPrefs.setDateStyle(action.config.dateStyle)

                    montageConfigPrefs.setVideoQuality(action.config.videoQuality)
                    montageConfigPrefs.setStabilizeFaces(action.config.stabilizeFaces)
                    montageConfigPrefs.setBackgroundColor(action.config.backgroundColor)
                    montageConfigPrefs.setWaterMark(action.config.waterMark)
                }
            }

            is ProjectAction.OnUpdateReminder -> viewModelScope.launch {
                val newProject = _state.value.project!!.copy(alarm = action.alarmData)

                repository.upsertProject(newProject)
                _state.update { it.copy(project = newProject) }

                if (action.alarmData == null) {
                    scheduler.cancel(newProject)
                } else {
                    scheduler.schedule(newProject)
                }
            }

            ProjectAction.OnResetMontagePrefs -> viewModelScope.launch {
                montageConfigPrefs.resetPrefs()
            }

            ProjectAction.OnShowPaywall -> stateLayer.settingsState.update { it.copy(showPaywall = true) }
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

    private suspend fun processDays() {
        state.value.days.forEach { day ->
            if (day.faceData == null) {
                val faceData = faceDetector.getFaceDataFromUri(day.image.toUri())
                repository.upsertDay(day.copy(faceData = faceData))
            }
        }
    }

    private fun observeConfig() = viewModelScope.launch {
        montageConfigPrefs.getFpiFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(framesPerImage = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getFpsFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(framesPerSecond = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getVideoQualityFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(videoQuality = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getBackgroundColorFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(backgroundColor = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getWaterMarkFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(waterMark = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getShowDateFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(showDate = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getShowMessageFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(showMessage = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getFontFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(font = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getDateStyleFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(dateStyle = pref))
                }
            }.launchIn(this)

        montageConfigPrefs.getStabilizeFacesFlow()
            .onEach { pref ->
                _state.update {
                    it.copy(montageConfig = it.montageConfig.copy(stabilizeFaces = pref))
                }
            }.launchIn(this)
    }

    fun checkDays() {

    }

    override fun onCleared() {
        super.onCleared()
        _exoPlayer.value?.release()
    }
}
