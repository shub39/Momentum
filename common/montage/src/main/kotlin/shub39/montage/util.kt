package shub39.montage

import shub39.momentum.core.data_classes.MontageConfig
import shub39.momentum.core.toDimensions

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