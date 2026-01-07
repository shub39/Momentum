package shub39.momentum.core.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import org.koin.core.annotation.Single
import shub39.momentum.core.domain.data_classes.Day
import java.io.File

@Single
class ImageHandler(
    private val context: Context
) {
    fun copyImageToAppData(day: Day): Uri {
        val destDirectory = File(context.filesDir, day.projectId.toString())
        if (!destDirectory.exists()) destDirectory.mkdirs()

        val destFile = File(destDirectory, day.date.toString())
        context.contentResolver.openInputStream(day.image.toUri())?.use { inputStream ->
            destFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return destFile.toUri()
    }

    fun deleteDayImage(day: Day) {
        try {
            day.image.toUri().toFile().delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}