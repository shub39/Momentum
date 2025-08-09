package shub39.momentum.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.MontageConfig
import shub39.momentum.core.domain.interfaces.MontageMaker
import shub39.momentum.core.domain.interfaces.MontageState
import shub39.montage.Muxer
import shub39.montage.MuxingResult
import java.io.File

@Single(binds = [MontageMaker::class])
class MontageMakerImpl(
    private val context: Context
) : MontageMaker {
    override suspend fun createMontage(
        days: List<Day>,
        file: File,
        montageConfig: MontageConfig
    ): MontageState = withContext(Dispatchers.Default) {
        val muxer = Muxer(context, file)
        muxer.setMuxerConfig(muxer.getMuxerConfig().update(montageConfig))

        val images = uriStringsToCenteredBitmaps(
            uriStrings = days.sortedBy { it.date }.map { it.image },
            width = montageConfig.videoWidth,
            height = montageConfig.videoHeight
        )

        return@withContext when (val result = muxer.muxAsync(images)) {
            is MuxingResult.MuxingError -> {
                MontageState.Error(
                    message = result.message,
                    exception = result.exception
                )
            }

            is MuxingResult.MuxingSuccess -> {
                MontageState.Success(result.file)
            }
        }
    }

    private fun uriStringsToCenteredBitmaps(
        uriStrings: List<String>,
        width: Int,
        height: Int,
        backgroundColor: Int = Color.BLACK
    ): List<Bitmap> {
        val contentResolver = context.contentResolver
        return uriStrings.mapNotNull { uriString ->
            try {
                val uri = uriString.toUri()
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    if (originalBitmap != null) {
                        val canvasBitmap = createBitmap(width, height)
                        val canvas = Canvas(canvasBitmap)
                        canvas.drawColor(backgroundColor)

                        val scale = minOf(
                            width.toFloat() / originalBitmap.width,
                            height.toFloat() / originalBitmap.height
                        )

                        val scaledWidth = originalBitmap.width * scale
                        val scaledHeight = originalBitmap.height * scale

                        val left = ((width - scaledWidth) / 2f)
                        val top = ((height - scaledHeight) / 2f)

                        val dstRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
                        canvas.drawBitmap(originalBitmap, null, dstRect, null)

                        canvasBitmap
                    } else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}