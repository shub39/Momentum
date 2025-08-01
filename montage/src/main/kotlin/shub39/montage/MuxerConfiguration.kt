package shub39.montage

import android.media.MediaFormat
import java.io.File

data class MuxerConfiguration(
    val file: File,
    val videoWidth: Int = 1080,
    val videoHeight: Int = 1920,
    val mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
    val framesPerImage: Int = 1,
    val framesPerSecond: Float = 10f,
    val bitrate: Int = 1500000,
    val frameMuxer: FrameMuxer = Mp4FrameMuxer(file.absolutePath, framesPerSecond),
    val iFrameInterval: Int = 10
)

interface MuxingCompletionListener {
    fun onVideoSuccessful(file: File)
    fun onVideoError(error: Throwable)
}

sealed interface MuxingResult {
    data class MuxingSuccess(
        val file: File
    ) : MuxingResult

    data class MuxingError(
        val message: String,
        val exception: Exception
    ) : MuxingResult
}