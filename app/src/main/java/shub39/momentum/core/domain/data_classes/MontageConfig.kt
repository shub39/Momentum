package shub39.momentum.core.domain.data_classes

import android.media.MediaFormat
import androidx.compose.ui.graphics.Color
import shub39.momentum.core.domain.enums.Fonts
import java.time.format.FormatStyle

data class MontageConfig(
    val videoWidth: Int = 768,
    val videoHeight: Int = 1024,
    val mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
    val framesPerImage: Int = 1,
    val framesPerSecond: Float = 1f,
    val bitrate: Int = 1500000,
    val iFrameInterval: Int = 10,
    val backgroundColor: Color = Color.Black,
    val waterMark: Boolean = true,
    val showDate: Boolean = true,
    val showMessage: Boolean = true,
    val font: Fonts = Fonts.FIGTREE,
    val dateStyle: FormatStyle = FormatStyle.FULL
)