package shub39.montage

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaCodecList.REGULAR_CODECS
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import androidx.annotation.RawRes
import java.io.IOException
import java.nio.ByteBuffer

class FrameBuilder(
    private val context: Context,
    private val muxerConfig: MuxerConfiguration,
    @RawRes private val audioTrackResource: Int?
) {
    companion object {
        const val TAG = "FrameBuilder"
        const val VERBOSE: Boolean = false
        const val SECOND_IN_USEC = 1000000
        const val TIMEOUT_USEC = 10000
    }

    private val mediaFormat: MediaFormat = run {
        val format = MediaFormat.createVideoFormat(
            muxerConfig.mimeType, muxerConfig
                .videoWidth, muxerConfig.videoHeight
        )

        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, muxerConfig.bitrate)
        format.setFloat(MediaFormat.KEY_FRAME_RATE, muxerConfig.framesPerSecond)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, muxerConfig.iFrameInterval)
        format
    }

    private val mediaCodec: MediaCodec = run {
        val codecs = MediaCodecList(REGULAR_CODECS)
        MediaCodec.createByCodecName(codecs.findEncoderForFormat(mediaFormat))
    }

    private val bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
    private var frameMuxer: FrameMuxer = muxerConfig.frameMuxer

    private var surface: Surface? = null

    private var audioExtractor: MediaExtractor? = run {
        if (audioTrackResource != null) {
            val assetFileDescriptor: AssetFileDescriptor =
                context.resources.openRawResourceFd(audioTrackResource)
            val extractor = MediaExtractor()
            extractor.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            extractor
        } else {
            null
        }
    }

    /**
     * @throws IOException
     */
    fun start() {
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        surface = mediaCodec.createInputSurface()
        mediaCodec.start()
        drainCodec(false)
    }

    fun createFrame(image: Any) {
        for (i in 0 until muxerConfig.framesPerImage) {
            val canvas = createCanvas()
            when (image) {
                is Int -> {
                    Log.i(TAG, "Trying to decode as @DrawableRes")
                    val bitmap = BitmapFactory.decodeResource(context.resources, image)
                    drawBitmapAndPostCanvas(bitmap, canvas)
                }

                is Bitmap -> drawBitmapAndPostCanvas(image, canvas)
                is Canvas -> postCanvasFrame(image)
                else -> Log.e(
                    TAG,
                    "Image type $image is not supported. Try using a Canvas or a Bitmap"
                )
            }
        }
    }

    private fun createCanvas(): Canvas? {
        return surface?.lockHardwareCanvas()
    }

    /**
     *
     * @param canvas acquired from createCanvas()
     */
    private fun drawBitmapAndPostCanvas(bitmap: Bitmap, canvas: Canvas?) {
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
        postCanvasFrame(canvas)
    }

    /**
     *
     * @param canvas acquired from createCanvas()
     */
    private fun postCanvasFrame(canvas: Canvas?) {
        surface?.unlockCanvasAndPost(canvas)
        drainCodec(false)
    }

    /**
     * Extracts all pending data from the encoder.
     *
     *
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     *
     * Borrows heavily from https://bigflake.com/mediacodec/EncodeAndMuxTest.java.txt
     */
    private fun drainCodec(endOfStream: Boolean) {
        if (VERBOSE) Log.d(TAG, "drainCodec($endOfStream)")
        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder")
            mediaCodec.signalEndOfInputStream()
        }

        while (true) {
            val encoderStatus: Int =
                mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC.toLong())

            when {
                encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    if (!endOfStream) break
                    else if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS")
                }

                encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (frameMuxer.isStarted()) {
                        throw RuntimeException("format changed twice")
                    }
                    val newFormat: MediaFormat = mediaCodec.outputFormat
                    Log.d(TAG, "encoder output format changed: $newFormat")
                    frameMuxer.start(newFormat, audioExtractor)
                }

                encoderStatus < 0 -> {
                    Log.wtf(
                        TAG,
                        "unexpected result from encoder.dequeueOutputBuffer: $encoderStatus"
                    )
                }

                else -> {
                    val encodedData: ByteBuffer =
                        mediaCodec.getOutputBuffer(encoderStatus)
                            ?: throw RuntimeException("getOutputBuffer($encoderStatus) returned null")

                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG")
                        bufferInfo.size = 0
                    }

                    if (bufferInfo.size != 0) {
                        if (!frameMuxer.isStarted()) {
                            throw RuntimeException("muxer hasn't started")
                        }

                        // Adjust position and limit before passing to muxer
                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)

                        frameMuxer.muxVideoFrame(encodedData, bufferInfo)

                        if (VERBOSE) Log.d(TAG, "sent ${bufferInfo.size} bytes to muxer")
                    }

                    mediaCodec.releaseOutputBuffer(encoderStatus, false)

                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        if (!endOfStream) {
                            Log.w(TAG, "reached end of stream unexpectedly")
                        } else if (VERBOSE) {
                            Log.d(TAG, "end of stream reached")
                        }
                        break
                    }
                }
            }
        }
    }

    fun muxAudioFrames() {
        val sampleSize = 256 * 1024
        val offset = 100
        val audioBuffer = ByteBuffer.allocate(sampleSize)
        val audioBufferInfo = MediaCodec.BufferInfo()
        var sawEOS = false
        audioExtractor!!.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        var finalAudioTime: Long
        val finalVideoTime: Long = frameMuxer.getVideoTime()
        var audioTrackFrameCount = 0
        while (!sawEOS) {
            audioBufferInfo.offset = offset
            audioBufferInfo.size = audioExtractor!!.readSampleData(audioBuffer, offset)
            if (audioBufferInfo.size < 0) {
                if (VERBOSE) Log.d(TAG, "Saw input EOS.")
                audioBufferInfo.size = 0
                sawEOS = true
            } else {
                finalAudioTime = audioExtractor!!.sampleTime
                audioBufferInfo.presentationTimeUs = finalAudioTime
                frameMuxer.muxAudioFrame(audioBuffer, audioBufferInfo)
                audioExtractor!!.advance()
                audioTrackFrameCount++
                if (VERBOSE) Log.d(
                    TAG,
                    "Frame ($audioTrackFrameCount Flags: ${audioBufferInfo.flags} Size(KB): ${audioBufferInfo.size / 1024}"
                )
                if ((finalAudioTime > finalVideoTime) &&
                    (finalAudioTime % finalVideoTime > muxerConfig.framesPerImage * SECOND_IN_USEC)
                ) {
                    sawEOS = true
                    if (VERBOSE) Log.d(
                        TAG,
                        "Final audio time: $finalAudioTime video time: $finalVideoTime"
                    )
                }
            }
        }
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    fun releaseVideoCodec() {
        // Release the video layer
        if (VERBOSE) Log.d(TAG, "releasing encoder objects")
        drainCodec(true)
        mediaCodec.stop()
        mediaCodec.release()
        surface?.release()
    }

    fun releaseAudioExtractor() {
        audioExtractor?.release()
    }

    fun releaseMuxer() {
        // Release MediaMuxer
        frameMuxer.release()
    }

}