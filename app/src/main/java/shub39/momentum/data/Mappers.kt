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
package shub39.momentum.data

import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.Project
import shub39.momentum.data.database.DayEntity
import shub39.momentum.data.database.ProjectEntity

fun ProjectEntity.toProject(): Project {
    return Project(id = id, title = title, description = description, alarm = alarm)
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(id = id, title = title, description = description, alarm = alarm)
}

fun DayEntity.toDay(): Day {
    return Day(
        id = id,
        projectId = projectId,
        date = date,
        image = image,
        comment = comment,
        isFavorite = isFavorite,
        faceData = faceData,
    )
}

fun Day.toDayEntity(): DayEntity {
    return DayEntity(
        id = id,
        projectId = projectId,
        date = date,
        image = image,
        comment = comment,
        isFavorite = isFavorite,
        faceData = faceData,
    )
}
