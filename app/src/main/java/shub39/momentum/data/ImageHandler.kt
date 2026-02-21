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
package shub39.momentum.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.File
import kotlin.time.Clock
import org.koin.core.annotation.Single
import shub39.momentum.core.data_classes.Day

@Single
class ImageHandler(private val context: Context) {
    fun copyImageToAppData(day: Day): Uri {
        val destDirectory = File(context.filesDir, day.projectId.toString())
        if (!destDirectory.exists()) destDirectory.mkdirs()

        val destFile = File(destDirectory, "${day.date}_${Clock.System.now().epochSeconds}")

        destDirectory.listFiles()?.find { it.name.startsWith("${day.date}_") }?.delete()

        context.contentResolver.openInputStream(day.image.toUri())?.use { inputStream ->
            destFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
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
