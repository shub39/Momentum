package shub39.momentum.core.data

import shub39.momentum.core.data.database.DayEntity
import shub39.momentum.core.data.database.ProjectEntity
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project

fun ProjectEntity.toProject(): Project {
    return Project(
        id = id,
        index = index,
        title = title,
        description = description,
        startDate = startDate,
        lastUpdatedDate = lastUpdatedDate,
        goalDate = goalDate
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        index = index,
        title = title,
        description = description,
        startDate = startDate,
        lastUpdatedDate = lastUpdatedDate,
        goalDate = goalDate
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