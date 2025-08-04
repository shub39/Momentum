package shub39.momentum.project.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import shub39.momentum.core.domain.interfaces.MontageResult
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectMontageView(
    state: ProjectState.Loaded,
    onAction: (ProjectAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(ProjectAction.OnCreateMontage(state.days))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state.montage) {
            is MontageResult.Error -> Text("Error")
            is MontageResult.Success -> {
                val videoPlayerHost =
                    remember { MediaPlayerHost(mediaUrl = state.montage.file.absolutePath) }
                VideoPlayerComposable(
                    playerHost = videoPlayerHost,
                    modifier = Modifier.fillMaxSize()
                )
            }

            null -> LoadingIndicator()
        }
    }
}