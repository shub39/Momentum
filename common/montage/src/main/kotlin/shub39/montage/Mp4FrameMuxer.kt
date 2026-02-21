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

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

internal class Mp4FrameMuxer(path: String, fps: Float) : FrameMuxer {

    companion object {
        private const val TAG = "Mp4FrameMuxer"
    }

    private val frameUsec: Long = (TimeUnit.SECONDS.toMicros(1L) / fps).toLong()
    private val muxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

    private var started = false
    private var videoTrackIndex: Int = -1
    private var audioTrackIndex: Int = -1
    private var videoFrames: Long = 0
    private var finalVideoTime: Long = 0

    override fun isStarted(): Boolean = started

    override fun start(videoFormat: MediaFormat, audioExtractor: MediaExtractor?) {
        audioExtractor?.let {
            try {
                it.selectTrack(0)
                val audioFormat = it.getTrackFormat(0)
                audioTrackIndex = muxer.addTrack(audioFormat)
                Log.d(TAG, "Added audio track: $audioFormat")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to add audio track: ${e.message}")
            }
        }

        videoTrackIndex = muxer.addTrack(videoFormat)
        Log.d(TAG, "Added video track: $videoFormat")

        muxer.start()
        started = true
    }

    override fun muxVideoFrame(encodedData: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        if (!started) throw IllegalStateException("Muxer not started")

        finalVideoTime = frameUsec * videoFrames++
        bufferInfo.presentationTimeUs = finalVideoTime

        encodedData.position(bufferInfo.offset)
        encodedData.limit(bufferInfo.offset + bufferInfo.size)

        muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
    }

    override fun muxAudioFrame(encodedData: ByteBuffer, audioBufferInfo: MediaCodec.BufferInfo) {
        if (audioTrackIndex < 0) return
        encodedData.position(audioBufferInfo.offset)
        encodedData.limit(audioBufferInfo.offset + audioBufferInfo.size)
        muxer.writeSampleData(audioTrackIndex, encodedData, audioBufferInfo)
    }

    override fun release() {
        try {
            if (started) {
                muxer.stop()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping muxer: ${e.message}")
        } finally {
            try {
                muxer.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error releasing muxer: ${e.message}")
            }
            started = false
        }
    }

    override fun getVideoTime(): Long = finalVideoTime
}
