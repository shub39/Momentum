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

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import shub39.momentum.R

@Composable
fun Camera(
    surfaceRequest: SurfaceRequest?,
    showGuides: Boolean,
    onToggleCamera: () -> Unit,
    onToggleGuides: () -> Unit,
    onTakePhoto: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(surfaceRequest = request, modifier = Modifier.fillMaxSize())
        }

        if (showGuides) {
            CameraGuides()
        }

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart),
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = "Back",
                tint = Color.White,
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
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_grid),
                        contentDescription = null,
                    )
                }

                FilledTonalIconButton(
                    onClick = onTakePhoto,
                    modifier =
                        Modifier.size(
                            IconButtonDefaults.largeContainerSize(
                                IconButtonDefaults.IconButtonWidthOption.Wide
                            )
                        ),
                    shapes = IconButtonDefaults.shapes(),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.camera),
                        contentDescription = "Take Photo",
                        modifier = Modifier.size(IconButtonDefaults.largeIconSize),
                    )
                }

                FilledTonalIconButton(onClick = onToggleCamera) {
                    Icon(
                        painter = painterResource(R.drawable.sync),
                        contentDescription = "Switch Camera",
                    )
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
