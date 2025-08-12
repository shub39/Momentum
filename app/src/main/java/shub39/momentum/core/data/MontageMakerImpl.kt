package shub39.momentum.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.MontageConfig
import shub39.momentum.core.domain.enums.DateStyle.Companion.toFormatStyle
import shub39.momentum.core.domain.enums.VideoQuality.Companion.toDimensions
import shub39.momentum.core.domain.interfaces.MontageMaker
import shub39.momentum.core.domain.interfaces.MontageState
import shub39.montage.Muxer
import shub39.montage.MuxingResult
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        val dimensions = config.videoQuality.toDimensions()

        val paint = Paint().apply {
            color = Color.WHITE
            textSize = dimensions.first * 0.04f
            alpha = 255
            isAntiAlias = true
            style = Paint.Style.FILL
            typeface = ResourcesCompat.getFont(context, config.font.fontRes)
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }

        val contentResolver = context.contentResolver
        return days.mapNotNull { day ->
            try {
                val uri = day.image.toUri()
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    if (originalBitmap != null) {
                        val canvasBitmap = createBitmap(dimensions.first, dimensions.second)
                        val canvas = Canvas(canvasBitmap)
                        canvas.drawColor(config.backgroundColor.toArgb())

                        val scale = minOf(
                            dimensions.first.toFloat() / originalBitmap.width,
                            dimensions.second.toFloat() / originalBitmap.height
                        )

                        val scaledWidth = originalBitmap.width * scale
                        val scaledHeight = originalBitmap.height * scale

                        val left = ((dimensions.first - scaledWidth) / 2f)
                        val top = ((dimensions.second - scaledHeight) / 2f)

                        val dstRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
                        canvas.drawBitmap(originalBitmap, null, dstRect, null)

                        if (config.waterMark) {
                            val watermark = "Momentum"
                            val paddingX = dimensions.first * 0.05f
                            val paddingY =
                                dimensions.second * 0.05f + paint.descent() + paint.textSize // Adjusted padding
                            canvas.drawText(watermark, paddingX, paddingY, paint)
                        }

                        if (config.showDate) {
                            val date = LocalDate.ofEpochDay(day.date).format(
                                DateTimeFormatter.ofLocalizedDate(config.dateStyle.toFormatStyle())
                            )
                            val paddingX = dimensions.first * 0.05f
                            val paddingY =
                                dimensions.second - dimensions.second * 0.05f - paint.descent()
                            canvas.drawText(date, paddingX, paddingY, paint)
                        }

                        if (config.showMessage && !day.comment.isNullOrBlank()) {
                            val message = day.comment
                            val paddingX = dimensions.first * 0.05f
                            val paddingY =
                                dimensions.second - dimensions.second * 0.12f - paint.descent()

                            canvas.drawText(message, paddingX, paddingY, paint)
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