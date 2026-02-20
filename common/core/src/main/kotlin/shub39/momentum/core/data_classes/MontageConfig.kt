package shub39.momentum.core.data_classes

import android.media.MediaFormat
import androidx.compose.ui.graphics.Color
import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.VideoQuality

data class MontageConfig(
    val mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
    val bitrate: Int = 10_000_000,
    val iFrameInterval: Int = 1,

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
    val censorFaces: Boolean = false
)