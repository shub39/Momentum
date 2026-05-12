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
package shub39.momentum.data.database

import androidx.compose.ui.graphics.Color
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.VideoQuality

@Entity(
    tableName = "options_table",
    foreignKeys =
        [
            ForeignKey(
                entity = ProjectEntity::class,
                parentColumns = ["id"],
                childColumns = ["projectId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("projectId")],
)
data class MontageOptionsEntity(
    @PrimaryKey val projectId: Long,
    val framesPerImage: Int = 1,
    val framesPerSecond: Float = 1f,
    val videoQuality: VideoQuality = VideoQuality.SMALL,
    val backgroundColor: Color = Color.Black,
    val waterMark: Boolean = true,
    val showDate: Boolean = true,
    val showMessage: Boolean = true,
    val font: Fonts = Fonts.FIGTREE,
    val dateStyle: DateStyle = DateStyle.FULL,
    val stabilizeFaces: Boolean = false,
    val censorFaces: Boolean = false,
)
