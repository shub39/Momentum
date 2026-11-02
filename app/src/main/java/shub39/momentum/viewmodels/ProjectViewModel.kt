/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.momentum.viewmodels

import android.util.Log
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlayer.Builder
import androidx.media3.exoplayer.ExoPlayer.REPEAT_MODE_ALL
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.toAndroidUri
import kotlin.time.TimeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.KoinViewModel
import shub39.momentum.core.data_classes.AnalyticsEvent
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageConfig
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.toMontageConfig
import shub39.momentum.core.data_classes.toMontageOptions
import shub39.momentum.core.enums.VideoAction
import shub39.momentum.core.interfaces.AlarmScheduler
import shub39.momentum.core.interfaces.AnalyticsWrapper
import shub39.momentum.core.interfaces.FaceDetector
import shub39.momentum.core.interfaces.MontageMaker
import shub39.momentum.core.interfaces.MontageState
import shub39.momentum.core.interfaces.ProjectRepository
import shub39.momentum.core.interfaces.ProjectSelection
import shub39.momentum.data.ImageHandler
import shub39.momentum.data.MontageHandler
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.project.ProjectState
import shub39.momentum.presentation.project.ScanState

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class ProjectViewModel(
    private val selection: ProjectSelection,
    private val montageMaker: MontageMaker,
    private val faceDetector: FaceDetector,
    private val repository: ProjectRepository,
    private val scheduler: AlarmScheduler,
    private val imageHandler: ImageHandler,
    private val montageHandler: MontageHandler,
    private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private val _exoPlayer = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayer =
        _exoPlayer
            .asStateFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    /** The only state without a persistent owner: in-flight montages and face scans. */
    private val ephemeral = MutableStateFlow(ProjectEphemeral())

    private val content: Flow<ProjectContent> =
        selection.selectedProjectId.flatMapLatest { projectId ->
            if (projectId == null) {
                flowOf(ProjectContent())
            } else {
                combine(
                    repository.getProjectByIdFlow(projectId),
                    repository.getDaysByProjectId(projectId),
                    repository.getMontageOptionsFlow(projectId),
                ) { project, days, options ->
                    ProjectContent(
                        project = project,
                        days = days,
                        montageConfig = options?.toMontageConfig() ?: MontageConfig(),
                    )
                }
            }
        }

    val state =
        combine(content, ephemeral) { content, ephemeral ->
                ProjectState(
                    project = content.project,
                    days = content.days,
                    montage = ephemeral.montage,
                    montageConfig = content.montageConfig,
                    scanState = ephemeral.scanState,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ProjectState(),
            )

    init {
        // Selecting another project discards everything scoped to the previous one
        viewModelScope.launch {
            selection.selectedProjectId.collect {
                _exoPlayer.value?.release()
                _exoPlayer.value = null
                ephemeral.value = ProjectEphemeral()
            }
        }
    }

    fun onAction(action: ProjectAction) {
        when (action) {
            is ProjectAction.OnUpdateCameraOptions ->
                viewModelScope.launch {
                    val currentProject = state.value.project ?: return@launch
                    repository.upsertProject(
                        currentProject.copy(cameraOptions = action.cameraOptions)
                    )
                }

            is OnUpdateProject ->
                viewModelScope.launch {
                    repository.upsertProject(action.project)
                    scheduler.schedule(action.project)
                }

            is OnDeleteProject ->
                viewModelScope.launch {
                    analytics.trackEvent(
                        AnalyticsEvent.PROJECT_DELETED,
                        mapOf("days_count" to state.value.days.size),
                    )
                    repository.deleteProject(action.project)
                    scheduler.cancel(action.project)
                    selection.clearSelection()
                }

            is OnDeleteDay ->
                viewModelScope.launch {
                    analytics.trackEvent(AnalyticsEvent.DAY_DELETED)
                    imageHandler.deleteDayImage(action.day)
                    repository.deleteDay(action.day)
                }

            is OnUpsertDay ->
                viewModelScope.launch {
                    analytics.trackEvent(
                        AnalyticsEvent.DAY_ADDED,
                        mapOf(
                            "is_new_image" to action.isNewImage,
                            "has_comment" to (action.day.comment?.isNotBlank() == true),
                        ),
                    )
                    if (action.isNewImage) {
                        val uri = PlatformFile(action.day.image).toAndroidUri()
                        val faceData = faceDetector.getFaceDataFromUri(uri)
                        val copiedImageUri = imageHandler.copyImageToAppData(action.day)

                        Log.d("ProjectViewModel", "faceData : $faceData")

                        repository.upsertDay(
                            action.day.copy(faceData = faceData, image = copiedImageUri.toString())
                        )
                    } else {
                        repository.upsertDay(action.day)
                    }
                }

            is OnCreateMontage ->
                viewModelScope.launch {
                    val startTime = TimeSource.Monotonic.markNow()
                    val projectId = selection.selectedProjectId.value ?: return@launch
                    val options = state.value.montageConfig.toMontageOptions(projectId)

                    val builtMontage =
                        montageHandler.getBuiltMontage(
                            projectId = projectId,
                            options = options,
                            days = action.days,
                        )
                    if (builtMontage != null) {
                        analytics.trackEvent(
                            AnalyticsEvent.MONTAGE_CREATED,
                            mapOf(
                                "days_count" to action.days.size,
                                "time_taken_ms" to startTime.elapsedNow().inWholeMilliseconds,
                                "fps" to state.value.montageConfig.framesPerSecond,
                                "video_quality" to state.value.montageConfig.videoQuality.name,
                                "is_cached" to true,
                            ),
                        )
                        _exoPlayer.value?.apply {
                            clearMediaItems()
                            setMediaItem(MediaItem.fromUri(builtMontage.toUri()))
                            prepare()
                        }

                        ephemeral.update {
                            it.copy(
                                montage =
                                    MontageState.Success(
                                        file = builtMontage,
                                        config = state.value.montageConfig,
                                    )
                            )
                        }
                    } else {
                        montageMaker
                            .createMontageFlow(
                                days = action.days,
                                config = state.value.montageConfig,
                            )
                            .flowOn(Dispatchers.Default)
                            .collect { montageState ->
                                Log.d("ProjectViewModel", "Montage state: $montageState")

                                if (montageState is MontageState.Success) {
                                    analytics.trackEvent(
                                        AnalyticsEvent.MONTAGE_CREATED,
                                        mapOf(
                                            "days_count" to action.days.size,
                                            "time_taken_ms" to
                                                startTime.elapsedNow().inWholeMilliseconds,
                                            "fps" to state.value.montageConfig.framesPerSecond,
                                            "video_quality" to
                                                state.value.montageConfig.videoQuality.name,
                                            "is_cached" to false,
                                        ),
                                    )
                                    val file =
                                        montageHandler.copyMontageToFiles(
                                            montage = montageState.file,
                                            projectId = projectId,
                                            options = options,
                                            days = action.days,
                                        )

                                    _exoPlayer.value?.apply {
                                        clearMediaItems()
                                        setMediaItem(MediaItem.fromUri(file.toUri()))
                                        prepare()
                                    }

                                    ephemeral.update {
                                        it.copy(
                                            montage =
                                                montageState.copy(
                                                    file = file,
                                                    config = state.value.montageConfig,
                                                )
                                        )
                                    }
                                } else {
                                    ephemeral.update { it.copy(montage = montageState) }
                                }
                            }
                    }
                }

            OnClearMontageState -> {
                _exoPlayer.value?.release()
                _exoPlayer.value = null
                ephemeral.update { it.copy(montage = MontageState.ProcessingImages()) }
            }

            is OnPlayerAction -> {
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

            is OnInitializeExoPlayer -> {
                if (_exoPlayer.value == null) {
                    _exoPlayer.update {
                        Builder(action.context).build().apply {
                            prepare()
                            repeatMode = REPEAT_MODE_ALL
                        }
                    }
                }
            }

            is OnEditMontageConfig -> {
                viewModelScope.launch {
                    analytics.trackEvent(
                        AnalyticsEvent.MONTAGE_CONFIG_UPDATED,
                        mapOf(
                            "fps" to action.config.framesPerSecond,
                            "video_quality" to action.config.videoQuality.name,
                        ),
                    )
                    selection.selectedProjectId.value?.let { projectId ->
                        repository.upsertMontageOptions(action.config.toMontageOptions(projectId))
                    }
                }
            }

            is OnUpdateReminder ->
                viewModelScope.launch {
                    analytics.trackEvent(
                        AnalyticsEvent.REMINDER_UPDATED,
                        mapOf("is_enabled" to (action.alarmData != null)),
                    )
                    val newProject =
                        state.value.project?.copy(alarm = action.alarmData) ?: return@launch

                    repository.upsertProject(newProject)

                    if (action.alarmData == null) {
                        scheduler.cancel(newProject)
                    } else {
                        scheduler.schedule(newProject)
                    }
                }

            is OnResetMontagePrefs ->
                viewModelScope.launch {
                    selection.selectedProjectId.value?.let { projectId ->
                        repository.upsertMontageOptions(MontageConfig().toMontageOptions(projectId))
                    }
                }

            OnStartFaceScan ->
                viewModelScope.launch {
                    if (state.value.days.isEmpty()) return@launch

                    analytics.trackEvent(
                        AnalyticsEvent.FACE_SCAN_STARTED,
                        mapOf("days_count" to state.value.days.size),
                    )

                    ephemeral.update { it.copy(scanState = ScanState.Processing(0f)) }
                    val size = state.value.days.size

                    try {
                        withContext(Dispatchers.IO) {
                            state.value.days.fastForEachIndexed { index, day ->
                                ephemeral.update {
                                    it.copy(
                                        scanState =
                                            ScanState.Processing(index.toFloat() / size.toFloat())
                                    )
                                }

                                val uri = PlatformFile(day.image).toAndroidUri()
                                val faceData = faceDetector.getFaceDataFromUri(uri)
                                repository.upsertDay(day.copy(faceData = faceData))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ProjectViewModel", "Error while scanning project for faces: ", e)
                    } finally {
                        ephemeral.update { it.copy(scanState = ScanState.Done) }
                    }
                }

            OnResetScanState -> {
                ephemeral.update { it.copy(scanState = ScanState.Idle) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _exoPlayer.value?.release()
    }
}

/** Project data owned by [ProjectRepository]; the only flow this screen derives from. */
private data class ProjectContent(
    val project: Project? = null,
    val days: List<Day> = emptyList(),
    val montageConfig: MontageConfig = MontageConfig(),
)

/** In-flight state that belongs to the session, not to the database. */
private data class ProjectEphemeral(
    val montage: MontageState = MontageState.ProcessingImages(),
    val scanState: ScanState = ScanState.Idle,
)
