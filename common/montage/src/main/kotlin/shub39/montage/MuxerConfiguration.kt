/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.montage

import android.media.MediaFormat
import java.io.File

enum class SlideAnimation {
    NONE,
    FADE_IN,
    SCALE_IN,
    SLIDE_LEFT,
    SLIDE_RIGHT,
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
)

fun MuxerConfiguration.createFrameMuxer(): FrameMuxer =
    Mp4FrameMuxer(file.absolutePath, framesPerSecond)

interface MuxingCompletionListener {
    fun onVideoSuccessful(file: File)

    fun onVideoError(error: Throwable)
}

sealed interface MuxingResult {
    data class MuxingSuccess(val file: File) : MuxingResult

    data class MuxingError(val message: String, val exception: Exception) : MuxingResult
}
