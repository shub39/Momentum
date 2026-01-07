package shub39.momentum.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toRectF
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.MontageConfig
import shub39.momentum.core.domain.data_classes.isValid
import shub39.momentum.core.domain.data_classes.toRect
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
        config: MontageConfig
    ): Flow<MontageState> = flow {
        val file = File(context.cacheDir, "temp.mp4")
        val muxer = Muxer(context, file)
        muxer.setMuxerConfig(muxer.getMuxerConfig().update(config))

        val sortedDays = days.sortedBy { it.date }
        val total = sortedDays.size

        val images = mutableListOf<Bitmap>()

        sortedDays.forEachIndexed { index, day ->
            val bitmap = processDay(day, config)
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
            contentResolver.openInputStream(uri)?.use { exifStream ->
                val exif = ExifInterface(exifStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                contentResolver.openInputStream(uri)?.use { bitmapSteam ->
                    val decodedBitmap = BitmapFactory.decodeStream(bitmapSteam)
                    val originalBitmap = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> {
                            rotateBitmap(decodedBitmap, 90f)
                        }

                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            rotateBitmap(decodedBitmap, 180f)
                        }

                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            rotateBitmap(decodedBitmap, 270f)
                        }

                        else -> decodedBitmap
                    }
                    if (originalBitmap != null) {
                        val canvasBitmap = createBitmap(dimensions.first, dimensions.second)
                        val canvas = Canvas(canvasBitmap)
                        canvas.drawColor(config.backgroundColor.toArgb())

                        val matrix = Matrix()

                        if (config.stabilizeFaces && day.faceData.isValid()) {
                            val faceBox = day.faceData?.toRect()!!
                            val targetFaceHeight = dimensions.second * 0.3f
                            val scale = targetFaceHeight / faceBox.height().toFloat()

                            val faceCenterX = faceBox.centerX().toFloat()
                            val faceCenterY = faceBox.centerY().toFloat()

                            val targetCenterX = dimensions.first / 2f
                            val targetCenterY = dimensions.second / 2f

                            // --- Build transform ---
                            matrix.postTranslate(
                                -faceCenterX,
                                -faceCenterY
                            ) // move face center to (0,0)
                            matrix.postScale(
                                scale,
                                scale
                            )                      // scale to target size
                            matrix.postRotate(-day.faceData.headAngle)          // straighten roll
                            matrix.postTranslate(targetCenterX, targetCenterY)  // center face
                        } else {
                            // No stabilization, just fit into canvas
                            val scale = minOf(
                                dimensions.first.toFloat() / originalBitmap.width,
                                dimensions.second.toFloat() / originalBitmap.height
                            )
                            val dx = (dimensions.first - originalBitmap.width * scale) / 2f
                            val dy = (dimensions.second - originalBitmap.height * scale) / 2f
                            matrix.postScale(scale, scale)
                            matrix.postTranslate(dx, dy)
                        }

                        canvas.drawBitmap(originalBitmap, matrix, null)

                        // --- Watermark, date, message ---
                        if (config.waterMark) {
                            val watermark = "Momentum"
                            val paddingX = dimensions.first * 0.05f
                            val paddingY =
                                dimensions.second * 0.05f + paint.descent() + paint.textSize
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

                        // censor face
                        if (config.censorFaces && config.stabilizeFaces && day.faceData.isValid()) {
                            val faceBox = day.faceData?.toRect()!!
                            val faceRectF = faceBox.toRectF()
                            matrix.mapRect(faceRectF)

                            val censorPaint = Paint().apply {
                                isAntiAlias = true
                                style = Paint.Style.FILL
                                color = config.backgroundColor.toArgb()
                            }
                            canvas.drawRect(faceRectF, censorPaint)
                        }

                        canvasBitmap
                    } else null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}