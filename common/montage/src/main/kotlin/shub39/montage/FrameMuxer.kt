package shub39.montage

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

interface FrameMuxer {
    fun isStarted(): Boolean
    fun start(videoFormat: MediaFormat, audioExtractor: MediaExtractor? = null)
    fun muxVideoFrame(encodedData: ByteBuffer, bufferInfo: MediaCodec.BufferInfo)
    fun muxAudioFrame(encodedData: ByteBuffer, audioBufferInfo: MediaCodec.BufferInfo)
    fun release()
    fun getVideoTime(): Long
}