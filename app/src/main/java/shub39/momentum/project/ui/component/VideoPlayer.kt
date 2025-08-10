package shub39.momentum.project.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import shub39.momentum.core.domain.data_classes.PlayerAction
import shub39.momentum.core.domain.enums.VideoAction

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VideoPlayer(
    exoPlayer: ExoPlayer,
    onPlayerAction: (PlayerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }

    // Update position periodically
    LaunchedEffect(exoPlayer) {
        delay(300L)
        onPlayerAction(PlayerAction(action = VideoAction.PLAY))
        while (isActive) {
            duration = exoPlayer.duration.coerceAtLeast(0L)
            position = exoPlayer.currentPosition.coerceAtLeast(0L)
            delay(500L)
        }
    }

    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000L)
            showControls = false
        }
    }

    Box(
        modifier = modifier.clickable { showControls = true }
    ) {
        PlayerSurface(
            player = exoPlayer,
            modifier = Modifier.fillMaxSize()
        )

        if (showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledIconButton(
                    onClick = {
                        if (exoPlayer.isPlaying) {
                            onPlayerAction(PlayerAction(action = VideoAction.PAUSE))
                            isPlaying = false
                        } else {
                            onPlayerAction(PlayerAction(action = VideoAction.PLAY))
                            isPlaying = true
                        }
                        showControls = true
                    }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }

                Slider(
                    value = if (duration > 0) position / duration.toFloat() else 0f,
                    onValueChange = { sliderValue ->
                        val newPosition = (sliderValue * duration).toLong()
                        onPlayerAction(PlayerAction(action = VideoAction.SEEK, data = newPosition))
                        position = newPosition
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

//    DisposableEffect() { }
}

