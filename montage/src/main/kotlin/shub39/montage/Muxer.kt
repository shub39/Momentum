package shub39.montage

import android.content.Context
import androidx.annotation.RawRes
import java.io.File
import java.io.IOException

class Muxer(private val context: Context, private val file: File) {

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
