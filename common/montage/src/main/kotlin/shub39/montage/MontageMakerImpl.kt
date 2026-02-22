/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.montage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toRectF
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageConfig
import shub39.momentum.core.data_classes.isValid
import shub39.momentum.core.data_classes.toRect
import shub39.momentum.core.interfaces.MontageMaker
import shub39.momentum.core.interfaces.MontageState
import shub39.momentum.core.toDimensions
import shub39.momentum.core.toFontRes
import shub39.momentum.core.toFormatStyle
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MontageMakerImpl(private val context: Context) : MontageMaker {

    override suspend fun createMontageFlow(
        days: List<Day>,
        config: MontageConfig,
    ): Flow<MontageState> = flow {
        val file = File(context.cacheDir, "temp.mp4")
        val muxer = Muxer(context, file)
        muxer.setMuxerConfig(muxer.getMuxerConfig().update(config))

        val sortedDays = days.sortedBy { it.date }
        val total = sortedDays.size

        val textPaint = Paint().apply {
            color = Color.WHITE
            alpha = 255
            isAntiAlias = true
            style = Paint.Style.FILL
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
            textSize = config.videoQuality.toDimensions().first * 0.04f
            config.font.toFontRes()?.let { typeface = ResourcesCompat.getFont(context, it) }
        }

        val censorPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = config.backgroundColor.toArgb()
        }

        var processedCount = 0
        val bitmapFlow = flow {
            sortedDays.forEach { day ->
                processDay(
                    day = day,
                    config = config,
                    textPaint = textPaint,
                    censorPaint = censorPaint
                )?.let { bitmap ->
                    emit(bitmap)
                }
            }
        }

        val flowForMuxer = bitmapFlow.onEach {
            processedCount++
            emit(MontageState.ProcessingImages(processedCount.toFloat() / total))
        }

        emit(MontageState.AssemblingVideo)

        when (val result = muxer.mux(flowForMuxer)) {
            is MuxingResult.MuxingError -> {
                emit(MontageState.Error(result.message, result.exception))
            }

            is MuxingResult.MuxingSuccess -> {
                emit(MontageState.Success(result.file, config))
            }
        }
    }

    private fun processDay(
        day: Day,
        config: MontageConfig,
        textPaint: Paint,
        censorPaint: Paint
    ): Bitmap? {
        val dimensions = config.videoQuality.toDimensions()
        val contentResolver = context.contentResolver

        return try {
            val uri = day.image.toUri()

            val orientation =
                contentResolver.openInputStream(uri)?.use { exifStream ->
                    ExifInterface(exifStream)
                        .getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED,
                        )
                } ?: ExifInterface.ORIENTATION_UNDEFINED

            contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val fd = pfd.fileDescriptor

                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFileDescriptor(fd, null, options)

                options.inSampleSize =
                    calculateInSampleSize(options, dimensions.first, dimensions.second)

                options.inJustDecodeBounds = false
                val decodedBitmap = BitmapFactory.decodeFileDescriptor(fd, null, options)

                val originalBitmap =
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> {
                            rotateBitmap(decodedBitmap, 90f).also { decodedBitmap.recycle() }
                        }

                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            rotateBitmap(decodedBitmap, 180f).also { decodedBitmap.recycle() }
                        }

                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            rotateBitmap(decodedBitmap, 270f).also { decodedBitmap.recycle() }
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
                            -faceCenterY,
                        ) // move face center to (0,0)
                        matrix.postScale(scale, scale) // scale to target size
                        matrix.postRotate(-day.faceData!!.headAngle) // straighten roll
                        matrix.postTranslate(targetCenterX, targetCenterY) // center face
                    } else {
                        // No stabilization, just fit into canvas
                        val scale =
                            minOf(
                                dimensions.first.toFloat() / originalBitmap.width,
                                dimensions.second.toFloat() / originalBitmap.height,
                            )
                        val dx = (dimensions.first - originalBitmap.width * scale) / 2f
                        val dy = (dimensions.second - originalBitmap.height * scale) / 2f
                        matrix.postScale(scale, scale)
                        matrix.postTranslate(dx, dy)
                    }

                    canvas.drawBitmap(originalBitmap, matrix, null)
                    originalBitmap.recycle()

                    // --- Watermark, date, message ---
                    if (config.waterMark) {
                        val watermark = "Momentum"
                        val paddingX = dimensions.first * 0.05f
                        val paddingY =
                            dimensions.second * 0.05f + textPaint.descent() + textPaint.textSize
                        canvas.drawText(watermark, paddingX, paddingY, textPaint)
                    }
                    if (config.showDate) {
                        val date =
                            LocalDate.ofEpochDay(day.date)
                                .format(
                                    DateTimeFormatter.ofLocalizedDate(
                                        config.dateStyle.toFormatStyle()
                                    )
                                )
                        val paddingX = dimensions.first * 0.05f
                        val paddingY =
                            dimensions.second - dimensions.second * 0.05f - textPaint.descent()
                        canvas.drawText(date, paddingX, paddingY, textPaint)
                    }
                    if (config.showMessage && !day.comment.isNullOrBlank()) {
                        val message = day.comment!!
                        val paddingX = dimensions.first * 0.05f
                        val paddingY =
                            dimensions.second - dimensions.second * 0.12f - textPaint.descent()
                        canvas.drawText(message, paddingX, paddingY, textPaint)
                    }

                    // censor face
                    if (config.censorFaces && config.stabilizeFaces && day.faceData.isValid()) {
                        val faceBox = day.faceData?.toRect()!!
                        val faceRectF = faceBox.toRectF()
                        matrix.mapRect(faceRectF)

                        canvas.drawRect(faceRectF, censorPaint)
                    }

                    canvasBitmap
                } else null
            }
        } catch (e: Exception) {
            Log.e("MontageMaker", "Error processing day: ", e)
            null
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
