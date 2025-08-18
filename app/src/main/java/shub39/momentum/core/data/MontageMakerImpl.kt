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
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import shub39.momentum.R
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

    override suspend fun createMontageFlow(
        days: List<Day>,
        file: File,
        config: MontageConfig
    ): Flow<MontageState> = flow {
        val muxer = Muxer(context, file)
        muxer.setMuxerConfig(muxer.getMuxerConfig().update(config))

        val detectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
        val faceDetector = FaceDetection.getClient(detectorOptions)

        val sortedDays = days.sortedBy { it.date }
        val total = sortedDays.size

        val images = mutableListOf<Bitmap>()

        sortedDays.forEachIndexed { index, day ->
            val bitmap = processDay(day, config, faceDetector)
            if (bitmap != null) images.add(bitmap)

            val fraction = (index + 1).toFloat() / total
            val progress = 0.8f * fraction
            emit(MontageState.Processing(progress, context.getString(R.string.processing_images)))
        }

        emit(MontageState.Processing(0.9f, context.getString(R.string.assembling_video)))

        when (val result = muxer.muxAsync(images)) {
            is MuxingResult.MuxingError -> {
                emit(MontageState.Error(result.message, result.exception))
            }
            is MuxingResult.MuxingSuccess -> {
                emit(MontageState.Processing(1f, "Done"))
                emit(MontageState.Success(result.file, config))
            }
        }
    }

    private fun processDay(
        day: Day,
        config: MontageConfig,
        faceDetector: FaceDetector
    ): Bitmap? {
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

        return try {
            val uri = day.image.toUri()
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                if (originalBitmap != null) {
                    val canvasBitmap = createBitmap(dimensions.first, dimensions.second)
                    val canvas = Canvas(canvasBitmap)
                    canvas.drawColor(config.backgroundColor.toArgb())

                    // --- Face stabilization ---
                    val dstRect: RectF = if (config.stabilizeFaces) {
                        val image = InputImage.fromBitmap(originalBitmap, 0)
                        val faces = Tasks.await(faceDetector.process(image))

                        if (faces.isNotEmpty()) {
                            val face = faces.maxByOrNull {
                                it.boundingBox.width() * it.boundingBox.height()
                            }!!
                            val faceBox = face.boundingBox
                            val targetFaceHeight = dimensions.second * 0.3f
                            val scale = targetFaceHeight / faceBox.height().toFloat()

                            val faceCenterX = faceBox.centerX() * scale
                            val faceCenterY = faceBox.centerY() * scale

                            val targetCenterX = dimensions.first / 2f
                            val targetCenterY = dimensions.second / 2f

                            val offsetX = targetCenterX - faceCenterX
                            val offsetY = targetCenterY - faceCenterY

                            RectF(
                                offsetX,
                                offsetY,
                                offsetX + originalBitmap.width * scale,
                                offsetY + originalBitmap.height * scale
                            )
                        } else {
                            // fallback
                            val scale = minOf(
                                dimensions.first.toFloat() / originalBitmap.width,
                                dimensions.second.toFloat() / originalBitmap.height
                            )
                            val left = (dimensions.first - originalBitmap.width * scale) / 2f
                            val top = (dimensions.second - originalBitmap.height * scale) / 2f
                            RectF(
                                left,
                                top,
                                left + originalBitmap.width * scale,
                                top + originalBitmap.height * scale
                            )
                        }
                    } else {
                        val scale = minOf(
                            dimensions.first.toFloat() / originalBitmap.width,
                            dimensions.second.toFloat() / originalBitmap.height
                        )
                        val left = (dimensions.first - originalBitmap.width * scale) / 2f
                        val top = (dimensions.second - originalBitmap.height * scale) / 2f
                        RectF(
                            left,
                            top,
                            left + originalBitmap.width * scale,
                            top + originalBitmap.height * scale
                        )
                    }

                    canvas.drawBitmap(originalBitmap, null, dstRect, null)

                    // --- Watermark, date, message ---
                    if (config.waterMark) {
                        val watermark = "Momentum"
                        val paddingX = dimensions.first * 0.05f
                        val paddingY = dimensions.second * 0.05f + paint.descent() + paint.textSize
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