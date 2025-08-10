package shub39.momentum.core.domain.data_classes

import android.media.MediaFormat

class MontageConfig(
    val videoWidth: Int = 768,
    val videoHeight: Int = 1024,
    val mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
    val framesPerImage: Int = 1,
    val framesPerSecond: Float = 2f,
    val bitrate: Int = 1500000,
    val iFrameInterval: Int = 10
)