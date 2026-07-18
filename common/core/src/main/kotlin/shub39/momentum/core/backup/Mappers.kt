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
package shub39.momentum.core.backup

import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageOptions
import shub39.momentum.core.data_classes.Project

fun Day.toSchema(): DaySchema =
    DaySchema(
        id = id,
        projectId = projectId,
        image = image,
        comment = comment,
        date = date,
        isFavorite = isFavorite,
        faceData = faceData,
    )

fun Project.toSchema(): ProjectSchema =
    ProjectSchema(id = id, title = title, description = description, alarm = alarm)

fun DaySchema.toDay(): Day =
    Day(
        id = id,
        projectId = projectId,
        image = image,
        comment = comment,
        date = date,
        isFavorite = isFavorite,
        faceData = faceData,
    )

fun ProjectSchema.toProject(): Project =
    Project(id = id, title = title, description = description, alarm = alarm)

fun MontageOptionsSchema.toMontageOptions(): MontageOptions =
    MontageOptions(
        projectId = projectId,
        framesPerImage = framesPerImage,
        framesPerSecond = framesPerSecond,
        videoQuality = videoQuality,
        backgroundColor = backgroundColor,
        waterMark = waterMark,
        showDate = showDate,
        showMessage = showMessage,
        font = font,
        dateStyle = dateStyle,
        stabilizeFaces = stabilizeFaces,
        censorFaces = censorFaces,
    )

fun MontageOptions.toSchema(): MontageOptionsSchema =
    MontageOptionsSchema(
        projectId = projectId,
        framesPerImage = framesPerImage,
        framesPerSecond = framesPerSecond,
        videoQuality = videoQuality,
        backgroundColor = backgroundColor,
        waterMark = waterMark,
        showDate = showDate,
        showMessage = showMessage,
        font = font,
        dateStyle = dateStyle,
        stabilizeFaces = stabilizeFaces,
        censorFaces = censorFaces,
    )
