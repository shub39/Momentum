package shub39.momentum.presentation.project.ui.sections

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
import shub39.momentum.R
import shub39.momentum.domain.data_classes.Day
import shub39.momentum.domain.data_classes.PlayerAction
import shub39.momentum.domain.data_classes.Project
import shub39.momentum.domain.data_classes.Theme
import shub39.momentum.domain.enums.AppTheme
import shub39.momentum.domain.enums.VideoAction
import shub39.momentum.domain.interfaces.MontageState
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.project.ProjectState
import shub39.momentum.presentation.project.ui.component.MontageEditSheet
import shub39.momentum.presentation.project.ui.component.VideoPlayer
import shub39.momentum.presentation.shared.MomentumTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectMontageView(
    exoPlayer: ExoPlayer?,
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
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

    Scaffold(modifier = modifier) { padding ->
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

                is MontageState.Processing -> {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val animatedProgress by animateFloatAsState(
                            targetValue = state.montage.progress
                        )

                        LoadingIndicator()

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearWavyProgressIndicator(
                            progress = { animatedProgress }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = state.montage.status,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            HorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            (state.montage as? MontageState.Success)?.let { result ->
                                fileShareLauncher.launch(PlatformFile(result.file))
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
                            painter = painterResource(R.drawable.share),
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
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Close"
                    )
                }

                IconButton(
                    onClick = { showEditSheet = true }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
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
                        painter = painterResource(R.drawable.download),
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
                buttonEnabled = state.montageConfig != (state.montage as? MontageState.Success)?.config,
                isPlusUser = isPlusUser,
                onNavigateToPaywall = onNavigateToPaywall
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
            exoPlayer = null,
            isPlusUser = true,
            onNavigateToPaywall = {}
        )
    }
}