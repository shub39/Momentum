package shub39.montage

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

class Mp4FrameMuxer(
    path: String,
    private val fps: Float
) : FrameMuxer {

    private val frameUsec: Long = run {
        (TimeUnit.SECONDS.toMicros(1L) / fps).toLong()
    }

    private val muxer: MediaMuxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

    private var started = false
    private var videoTrackIndex = 0
    private var audioTrackIndex = 0
    private var videoFrames = 0
    private var finalVideoTime: Long = 0

    override fun isStarted(): Boolean = started

    override fun start(
        videoFormat: MediaFormat,
        audioExtractor: MediaExtractor?
    ) {
        audioExtractor?.selectTrack(0)
        val audioFormat = audioExtractor?.getTrackFormat(0)
        videoTrackIndex = muxer.addTrack(videoFormat)
        audioFormat?.run {
            audioTrackIndex = muxer.addTrack(audioFormat)
            Log.e("Audio format: %s", audioFormat.toString())
        }
        Log.d("Video format: %s", videoFormat.toString())
        muxer.start()
        started = true
    }

    override fun muxVideoFrame(
        encodedData: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo
    ) {
        finalVideoTime = frameUsec * videoFrames++
        bufferInfo.presentationTimeUs = finalVideoTime

        muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
    }

    override fun muxAudioFrame(
        encodedData: ByteBuffer,
        audioBufferInfo: MediaCodec.BufferInfo
    ) {
        muxer.writeSampleData(audioTrackIndex, encodedData, audioBufferInfo)
    }

    override fun release() {
        muxer.stop()
        muxer.release()
    }

    override fun getVideoTime(): Long {
        return finalVideoTime
    }

}