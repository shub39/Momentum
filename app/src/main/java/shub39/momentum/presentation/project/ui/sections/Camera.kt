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
package shub39.momentum.presentation.project.ui.sections

import android.annotation.SuppressLint
import android.app.Activity
import android.view.OrientationEventListener
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.SurfaceRequest
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import shub39.momentum.R

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun Camera(
    surfaceRequest: SurfaceRequest?,
    showGuides: Boolean,
    cameraSelector: CameraSelector,
    onToggleCamera: () -> Unit,
    onToggleGuides: () -> Unit,
    onTakePhoto: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val view = LocalView.current
    var rotation by remember { mutableFloatStateOf(0f) }

    DisposableEffect(view) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(window, view)

        controller.hide(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return
                val newRotation = when (orientation) {
                    !in 46..315 -> 0f
                    in 46..135 -> 270f
                    in 136..225 -> 180f
                    in 226..315 -> 90f
                    else -> 0f
                }
                if (rotation != newRotation) {
                    rotation = newRotation
                }
            }
        }
        listener.enable()

        onDispose {
            controller.show(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
            )
            listener.disable()
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        label = "IconRotation"
    )

    Box(modifier = modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            val isFrontCamera = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
            CameraXViewfinder(
                surfaceRequest = request,
                modifier =
                    Modifier.fillMaxSize().graphicsLayer {
                        if (isFrontCamera) {
                            scaleX = -1f
                        }
                    },
            )
        }

        if (showGuides) {
            CameraGuides()
        }

        Box(modifier = Modifier.systemBarsPadding()) {
            FilledTonalIconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .graphicsLayer { rotationZ = animatedRotation },
            ) {
                Icon(
                    painter = painterResource(R.drawable.nav_arrow_back),
                    contentDescription = "Back",
                )
            }

            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    FilledTonalIconToggleButton(
                        checked = showGuides,
                        onCheckedChange = { onToggleGuides() },
                        modifier = Modifier.graphicsLayer { rotationZ = animatedRotation },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_grid),
                            contentDescription = "Toggle Grid",
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onTakePhoto,
                        modifier =
                            Modifier
                                .size(
                                    IconButtonDefaults.largeContainerSize(
                                        IconButtonDefaults.IconButtonWidthOption.Wide
                                    )
                                )
                                .graphicsLayer { rotationZ = animatedRotation },
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "Take Photo",
                            modifier = Modifier.size(IconButtonDefaults.largeIconSize),
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onToggleCamera,
                        modifier = Modifier.graphicsLayer { rotationZ = animatedRotation },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sync),
                            contentDescription = "Switch Camera",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraGuides() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val color = Color.White.copy(alpha = 0.5f)
        val strokeWidth = 1.dp.toPx()

        // Vertical lines
        drawLine(
            color = color,
            start = Offset(width / 3, 0f),
            end = Offset(width / 3, height),
            strokeWidth = strokeWidth,
        )
        drawLine(
            color = color,
            start = Offset(2 * width / 3, 0f),
            end = Offset(2 * width / 3, height),
            strokeWidth = strokeWidth,
        )

        // Horizontal lines
        drawLine(
            color = color,
            start = Offset(0f, height / 3),
            end = Offset(width, height / 3),
            strokeWidth = strokeWidth,
        )
        drawLine(
            color = color,
            start = Offset(0f, 2 * height / 3),
            end = Offset(width, 2 * height / 3),
            strokeWidth = strokeWidth,
        )
    }
}
