package shub39.montage

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
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
        private const val TAG = "FrameBuilder"
        private const val TIMEOUT_USEC = 10_000L
    }

    private val mediaFormat: MediaFormat = MediaFormat.createVideoFormat(
        muxerConfig.mimeType, muxerConfig.videoWidth, muxerConfig.videoHeight
    ).apply {
        setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        setInteger(MediaFormat.KEY_BIT_RATE, muxerConfig.bitrate)
        setInteger(MediaFormat.KEY_FRAME_RATE, muxerConfig.framesPerSecond.toInt())
        setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, muxerConfig.iFrameInterval)
        setInteger(MediaFormat.KEY_COLOR_RANGE, MediaFormat.COLOR_RANGE_FULL)
        setInteger(MediaFormat.KEY_COLOR_STANDARD, MediaFormat.COLOR_STANDARD_BT709)
        setInteger(MediaFormat.KEY_COLOR_TRANSFER, MediaFormat.COLOR_TRANSFER_SDR_VIDEO)
    }

    private val mediaCodec: MediaCodec
    private val bufferInfo = MediaCodec.BufferInfo()
    private var surface: Surface? = null
    private var frameMuxer: FrameMuxer = muxerConfig.createFrameMuxer()
    private var audioExtractor: MediaExtractor? = null

    private var pushedFrames: Long = 0

    init {
        val codecs = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val codecName = codecs.findEncoderForFormat(mediaFormat)
            ?: throw IOException("No suitable codec for format: $mediaFormat")
        mediaCodec = MediaCodec.createByCodecName(codecName)

        audioTrackResource?.let { res ->
            val afd: AssetFileDescriptor = context.resources.openRawResourceFd(res)
            audioExtractor = MediaExtractor().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
        }
    }

    @Throws(IOException::class)
    fun start() {
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        surface = mediaCodec.createInputSurface()
        mediaCodec.start()

        drainCodec(false)
    }

    fun createFrame(image: Any) {
        for (i in 0 until muxerConfig.framesPerImage) {
            val canvas = lockCanvas() ?: continue
            when (image) {
                is Int -> {
                    val bitmap = BitmapFactory.decodeResource(context.resources, image)
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                }
                is Bitmap -> canvas.drawBitmap(image, 0f, 0f, null)
                else -> Log.e(TAG, "Unsupported image type: ${image::class.java}")
            }

            postCanvasFrame(canvas)

            Thread.sleep(1)
        }
    }

    private fun lockCanvas(): Canvas? = surface?.lockHardwareCanvas()

    private fun postCanvasFrame(canvas: Canvas?) {
        try {
            surface?.unlockCanvasAndPost(canvas)
        } catch (e: Exception) {
            Log.w(TAG, "unlockCanvasAndPost failed: ${e.message}")
        }

        drainCodec(false)
    }

    private fun drainCodec(endOfStream: Boolean) {
        if (endOfStream) {
            try {
                mediaCodec.signalEndOfInputStream()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Codec already released", e)
                return
            }
        }

        while (true) {
            when (val encoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC)) {
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    if (!endOfStream) return
                }

                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (frameMuxer.isStarted()) throw RuntimeException("Format changed twice")
                    val newFormat = mediaCodec.outputFormat
                    frameMuxer.start(newFormat, audioExtractor)
                }

                else -> {
                    if (encoderStatus < 0) {
                        Log.w(TAG, "Unexpected encoderStatus: $encoderStatus")
                        continue
                    }

                    val encoded = mediaCodec.getOutputBuffer(encoderStatus)
                        ?: throw RuntimeException("getOutputBuffer returned null")

                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        bufferInfo.size = 0
                    }

                    if (bufferInfo.size != 0) {
                        if (!frameMuxer.isStarted()) throw RuntimeException("Muxer hasn't started")

                        encoded.position(bufferInfo.offset)
                        encoded.limit(bufferInfo.offset + bufferInfo.size)

                        frameMuxer.muxVideoFrame(encoded, bufferInfo)
                        pushedFrames++
                    }

                    mediaCodec.releaseOutputBuffer(encoderStatus, false)

                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        if (!endOfStream) Log.w(TAG, "Reached EOS unexpectedly")
                        break
                    }
                }
            }
        }
    }

    fun muxAudioFrames() {
        val extractor = audioExtractor ?: return
        val sampleSize = 256 * 1024
        val audioBuffer = ByteBuffer.allocate(sampleSize)
        val audioBufferInfo = MediaCodec.BufferInfo()

        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

        while (true) {
            audioBufferInfo.offset = 0
            val read = extractor.readSampleData(audioBuffer, 0)
            if (read < 0) break
            audioBufferInfo.size = read
            audioBufferInfo.presentationTimeUs = extractor.sampleTime
            audioBuffer.position(audioBufferInfo.offset)
            audioBuffer.limit(audioBufferInfo.offset + audioBufferInfo.size)

            frameMuxer.muxAudioFrame(audioBuffer, audioBufferInfo)
            extractor.advance()
        }
    }

    fun releaseVideoCodec() {
        try {
            drainCodec(true)
        } catch (e: Exception) {
            Log.w(TAG, "drainCodec on release failed: ${e.message}")
        }
        try {
            mediaCodec.stop()
        } catch (e: Exception) { /* ignore */
        }
        try {
            mediaCodec.release()
        } catch (e: Exception) { /* ignore */
        }
        try {
            surface?.release()
        } catch (e: Exception) { /* ignore */
        }
    }

    fun releaseAudioExtractor() {
        audioExtractor?.release()
    }

    fun releaseMuxer() {
        frameMuxer.release()
    }
}