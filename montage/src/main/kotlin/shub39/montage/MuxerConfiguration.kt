package shub39.montage

import android.media.MediaFormat
import java.io.File

enum class SlideAnimation {
    NONE,
    FADE_IN,
    SCALE_IN,
    SLIDE_LEFT,
    SLIDE_RIGHT
}

data class MuxerConfiguration(
    val file: File,
    val videoWidth: Int = 1080,
    val videoHeight: Int = 1920,
    val mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
    val framesPerImage: Int = 1,
    val framesPerSecond: Float = 30f,
    val bitrate: Int = 5_000_000,
    val iFrameInterval: Int = 1,
    val animation: SlideAnimation = SlideAnimation.SLIDE_LEFT,
) {
    fun createFrameMuxer(): FrameMuxer = Mp4FrameMuxer(file.absolutePath, framesPerSecond)
}

interface MuxingCompletionListener {
    fun onVideoSuccessful(file: File)
    fun onVideoError(error: Throwable)
}

sealed interface MuxingResult {
    data class MuxingSuccess(val file: File) : MuxingResult
    data class MuxingError(val message: String, val exception: Exception) : MuxingResult
}