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
package shub39.momentum.presentation.shared

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

fun Modifier.zigZagBackground(
    zigZagHeight: Float = 20f,
    steps: Int = 20,
    lineColor: Color = Color.Gray.copy(alpha = 0.3f),
    strokeWidth: Float = 4f,
): Modifier =
    this.then(
        Modifier.drawBehind {
            val stepWidth = size.width / steps
            val midY = size.height / 2

            val path =
                Path().apply {
                    moveTo(0f, midY)
                    var x = 0f
                    var up = true
                    while (x < size.width) {
                        val nextX = x + stepWidth
                        val nextY = if (up) midY - zigZagHeight else midY + zigZagHeight
                        lineTo(nextX, nextY)
                        x = nextX
                        up = !up
                    }
                }

            drawPath(path = path, color = lineColor, style = Stroke(width = strokeWidth))
        }
    )
