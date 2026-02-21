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

import android.content.Context
import androidx.annotation.RawRes
import java.io.File
import java.io.IOException

internal class Muxer(private val context: Context, private val file: File) {

    private var muxerConfig: MuxerConfiguration = MuxerConfiguration(file)
    private var muxingCompletionListener: MuxingCompletionListener? = null

    fun setMuxerConfig(config: MuxerConfiguration) {
        this.muxerConfig = config
    }

    fun getMuxerConfig() = muxerConfig

    fun setOnMuxingCompletedListener(listener: MuxingCompletionListener) {
        this.muxingCompletionListener = listener
    }

    fun mux(imageList: List<Any>, @RawRes audioTrack: Int? = null): MuxingResult {
        val frameBuilder = FrameBuilder(context, muxerConfig, audioTrack)
        try {
            frameBuilder.start()
        } catch (e: IOException) {
            muxingCompletionListener?.onVideoError(e)
            return MuxingResult.MuxingError("Start encoder failed", e)
        }

        try {
            for (image in imageList) {
                frameBuilder.createFrame(image)
            }

            frameBuilder.releaseVideoCodec()

            if (audioTrack != null) frameBuilder.muxAudioFrames()

            frameBuilder.releaseAudioExtractor()
            frameBuilder.releaseMuxer()

            muxingCompletionListener?.onVideoSuccessful(file)
            return MuxingResult.MuxingSuccess(file)
        } catch (e: Exception) {
            try {
                frameBuilder.releaseVideoCodec()
            } catch (_: Exception) {
            }
            try {
                frameBuilder.releaseAudioExtractor()
            } catch (_: Exception) {
            }
            try {
                frameBuilder.releaseMuxer()
            } catch (_: Exception) {
            }

            muxingCompletionListener?.onVideoError(e)
            return MuxingResult.MuxingError("Muxing failed", Exception(e))
        }
    }

    fun muxAsync(imageList: List<Any>, @RawRes audioTrack: Int? = null): MuxingResult =
        mux(imageList, audioTrack)
}
