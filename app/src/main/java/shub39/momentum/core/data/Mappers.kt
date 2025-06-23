package shub39.momentum.core.data

import shub39.momentum.core.data.database.DayEntity
import shub39.momentum.core.data.database.ProjectEntity
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project

fun ProjectEntity.toProject(): Project {
    return Project(
        id = id,
        title = title,
        description = description,
        startDate = startDate,
        lastUpdatedDate = lastUpdatedDate,
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        title = title,
        description = description,
        startDate = startDate,
        lastUpdatedDate = lastUpdatedDate,
    )
}

fun DayEntity.toDays(): Day {
    return Day(
        id = id,
        projectId = projectId,
        date = date,
        image = image,
        comment = comment,
        isFavorite = isFavorite
    )
}

fun Day.toDayEntity(): DayEntity {
    return DayEntity(
        id = id,
        projectId = projectId,
        date = date,
        image = image,
        comment = comment,
        isFavorite = isFavorite
    )
}