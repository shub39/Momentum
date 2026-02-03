package shub39.momentum.data

import shub39.momentum.data.database.DayEntity
import shub39.momentum.data.database.ProjectEntity
import shub39.momentum.domain.data_classes.Day
import shub39.momentum.domain.data_classes.MontageConfig
import shub39.momentum.domain.data_classes.Project
import shub39.momentum.domain.enums.VideoQuality.Companion.toDimensions
import shub39.montage.MuxerConfiguration

fun MuxerConfiguration.update(montageConfig: MontageConfig): MuxerConfiguration {
    return copy(
        videoWidth = montageConfig.videoQuality.toDimensions().first,
        videoHeight = montageConfig.videoQuality.toDimensions().second,
        mimeType = montageConfig.mimeType,
        framesPerImage = montageConfig.framesPerImage,
        framesPerSecond = montageConfig.framesPerSecond,
        bitrate = montageConfig.bitrate,
        iFrameInterval = montageConfig.iFrameInterval
    )
}

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