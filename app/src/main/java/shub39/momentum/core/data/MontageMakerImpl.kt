package shub39.momentum.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.toArgb
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

        val images = daysToBitmaps(days.sortedBy { it.date }, montageConfig)

        return@withContext when (val result = muxer.muxAsync(images)) {
            is MuxingResult.MuxingError -> {
                MontageState.Error(
                    message = result.message,
                    exception = result.exception
                )
            }

            is MuxingResult.MuxingSuccess -> {
                MontageState.Success(result.file, montageConfig)
            }
        }
    }

    private fun daysToBitmaps(
        days: List<Day>,
        config: MontageConfig
    ): List<Bitmap> {
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = config.videoWidth * 0.05f
            alpha = 150
            isAntiAlias = true
            style = Paint.Style.FILL
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }

        val contentResolver = context.contentResolver
        return days.mapNotNull { day ->
            try {
                val uri = day.image.toUri()
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    if (originalBitmap != null) {
                        val canvasBitmap = createBitmap(config.videoWidth, config.videoHeight)
                        val canvas = Canvas(canvasBitmap)
                        canvas.drawColor(config.backgroundColor.toArgb())

                        val scale = minOf(
                            config.videoWidth.toFloat() / originalBitmap.width,
                            config.videoHeight.toFloat() / originalBitmap.height
                        )

                        val scaledWidth = originalBitmap.width * scale
                        val scaledHeight = originalBitmap.height * scale

                        val left = ((config.videoWidth - scaledWidth) / 2f)
                        val top = ((config.videoHeight - scaledHeight) / 2f)

                        val dstRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
                        canvas.drawBitmap(originalBitmap, null, dstRect, null)

                        if (config.waterMark) {
                            val watermark = "Momentum"
                            val paddingX = config.videoWidth * 0.05f
                            val paddingY = config.videoHeight * 0.05f + paint.descent()
                            canvas.drawText(watermark, paddingX, paddingY, paint)
                        }

                        if (config.showDate) {
                            val date = LocalDate.ofEpochDay(day.date).format(
                                DateTimeFormatter.ofLocalizedDate(
                                    FormatStyle.FULL
                                )
                            )
                            val paddingX = config.videoWidth * 0.05f
                            val paddingY =
                                config.videoHeight - config.videoHeight * 0.05f - paint.descent()
                            canvas.drawText(date, paddingX, paddingY, paint)
                        }

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