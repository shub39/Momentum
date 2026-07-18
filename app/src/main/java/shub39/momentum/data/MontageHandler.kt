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
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageOptions

@Single
class MontageHandler(private val context: Context) {
    companion object {
        private const val MONTAGE_FILE_NAME = "montage.mp4"
        private const val LOG_FILE_NAME = "log.json"

        @Serializable data class MontageLog(val days: List<Day>, val options: MontageOptions)
    }

    fun getBuiltMontage(projectId: Long, options: MontageOptions, days: List<Day>): File? {
        val destDir = File(context.filesDir, projectId.toString())
        val logFile = File(destDir, LOG_FILE_NAME)

        val currentLog = Json.encodeToString(MontageLog(days = days, options = options))

        return if (!logFile.exists() || logFile.readText() != currentLog) null
        else {
            val montage = File(destDir, MONTAGE_FILE_NAME)
            if (montage.exists()) montage else null
        }
    }

    fun copyMontageToFiles(
        montage: File,
        projectId: Long,
        options: MontageOptions,
        days: List<Day>,
    ): File {
        val destDir = File(context.filesDir, projectId.toString())
        if (!destDir.exists()) destDir.mkdirs()

        val destFile = File(destDir, MONTAGE_FILE_NAME)
        montage.copyTo(destFile, true)

        val log = File(destDir, LOG_FILE_NAME)
        log.writeText(Json.encodeToString(MontageLog(days = days, options = options)))

        return destFile
    }
}
