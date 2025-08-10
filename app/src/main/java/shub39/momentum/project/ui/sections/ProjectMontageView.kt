package shub39.momentum.project.ui.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberShareFileLauncher
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.launch
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.interfaces.MontageState
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import shub39.momentum.project.ui.component.VideoPlayer
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectMontageView(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

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
        onAction(ProjectAction.OnCreateMontage(state.days))
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
                    VideoPlayer(
                        file = state.montage.file,
                        modifier = Modifier
                            .size(330.dp, 440.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }

                MontageState.Processing -> LoadingIndicator()
            }

            HorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = state.montage is MontageState.Success,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        FloatingActionButton(
                            onClick = {
                                (state.montage as? MontageState.Success)?.let { result ->
                                    fileShareLauncher.launch(
                                        PlatformFile(result.file)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
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
            onNavigateBack = {}
        )
    }
}