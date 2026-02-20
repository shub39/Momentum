package shub39.momentum.data

import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.Project
import shub39.momentum.data.database.DayEntity
import shub39.momentum.data.database.ProjectEntity

fun ProjectEntity.toProject(): Project {
    return Project(
        id = id,
        title = title,
        description = description,
        alarm = alarm
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        title = title,
        description = description,
        alarm = alarm
    )
}

fun DayEntity.toDay(): Day {
    return Day(
        id = id,
        projectId = projectId,
        date = date,
        image = image,
        comment = comment,
        isFavorite = isFavorite,
        faceData = faceData
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
        faceData = faceData
    )
}