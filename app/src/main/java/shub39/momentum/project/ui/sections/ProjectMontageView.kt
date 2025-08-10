package shub39.momentum.project.ui.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberShareFileLauncher
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.launch
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.PlayerAction
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.VideoAction
import shub39.momentum.core.domain.interfaces.MontageState
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import shub39.momentum.project.ui.component.MontageEditSheet
import shub39.momentum.project.ui.component.VideoPlayer
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectMontageView(
    exoPlayer: ExoPlayer?,
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val lifeCycleOwner = LocalLifecycleOwner.current

    val fileShareLauncher = rememberShareFileLauncher()
    val fileSaverLauncher = rememberFileSaverLauncher { file ->
        if (file != null) {
            (state.montage as? MontageState.Success)?.let { result ->
                val platformFile = PlatformFile(result.file)
                scope.launch { file.write(platformFile) }
            }
        }
    }

    var showEditSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onAction(ProjectAction.OnInitializeExoPlayer(context))
        onAction(ProjectAction.OnCreateMontage(state.days))
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> onAction(
                    ProjectAction.OnPlayerAction(
                        PlayerAction(
                            VideoAction.PAUSE
                        )
                    )
                )

                Lifecycle.Event.ON_RESUME -> onAction(
                    ProjectAction.OnPlayerAction(
                        PlayerAction(
                            VideoAction.PLAY
                        )
                    )
                )

                else -> Unit
            }
        }

        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
            onAction(ProjectAction.OnClearMontageState)
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state.montage) {
                is MontageState.Error -> Text("Error: ${state.montage.message}")

                is MontageState.Success -> {
                    exoPlayer?.let { player ->
                        VideoPlayer(
                            exoPlayer = player,
                            onPlayerAction = { onAction(ProjectAction.OnPlayerAction(it)) },
                            modifier = Modifier
                                .size(330.dp, 440.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }
                }

                MontageState.Processing -> LoadingIndicator()
            }

            HorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            (state.montage as? MontageState.Success)?.let { result ->
                                fileShareLauncher.launch(
                                    PlatformFile(result.file)
                                )
                            }
                        },
                        containerColor = if (state.montage is MontageState.Success) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        contentColor = if (state.montage is MontageState.Success) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onTertiary
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                },
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.BottomCenter)
            ) {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }

                IconButton(
                    onClick = { showEditSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }

                IconButton(
                    onClick = {
                        fileSaverLauncher.launch(
                            suggestedName = state.project?.title ?: "Untitled",
                            extension = "mp4"
                        )
                    },
                    enabled = state.montage is MontageState.Success
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save to gallery"
                    )
                }
            }
        }

        if (showEditSheet) {
            MontageEditSheet(
                state = state,
                onAction = onAction,
                onDismissRequest = { showEditSheet = false },
                buttonEnabled = state.montageConfig != (state.montage as? MontageState.Success)?.config
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            ProjectState(
                project = Project(
                    id = 1,
                    title = "Sample Project",
                    description = "A sample project",
                ),
                days = (0..10).map {
                    Day(
                        id = it.toLong(),
                        projectId = 1,
                        image = "",
                        comment = it.toString(),
                        date = LocalDate.now().minusDays(it.toLong()).toEpochDay(),
                        isFavorite = false
                    )
                }
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        ProjectMontageView(
            state = state,
            onAction = {},
            onNavigateBack = {},
            exoPlayer = null
        )
    }
}