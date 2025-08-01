package shub39.momentum.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.interfaces.MontageMaker
import shub39.momentum.core.domain.interfaces.MontageResult
import shub39.montage.Muxer
import shub39.montage.MuxingResult
import java.io.File

class MontageMakerImpl(
    private val context: Context
) : MontageMaker {
    override suspend fun createMontage(days: List<Day>, file: File): MontageResult =
        withContext(Dispatchers.Default) {
            val muxer = Muxer(context, file)
            val images = uriStringsToBitmaps(days.map { it.image })

            return@withContext when (val result = muxer.muxAsync(images)) {
                is MuxingResult.MuxingError -> {
                    MontageResult.Error(
                        message = result.message,
                        exception = result.exception
                    )
                }

                is MuxingResult.MuxingSuccess -> {
                    MontageResult.Success(result.file)
                }
            }
        }

    private fun uriStringsToBitmaps(uriStrings: List<String>): List<Bitmap> {
        val contentResolver = context.contentResolver
        return uriStrings.mapNotNull { uriString ->
            try {
                val uri = uriString.toUri()
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream).also {
                    inputStream?.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}