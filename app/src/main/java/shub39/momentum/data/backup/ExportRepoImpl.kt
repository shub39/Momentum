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
package shub39.momentum.data.backup

import android.content.Context
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import shub39.momentum.core.backup.ExportRepo
import shub39.momentum.core.backup.ExportResult
import shub39.momentum.core.backup.ExportSchema
import shub39.momentum.core.backup.SCHEMA_FILE_NAME
import shub39.momentum.core.backup.toDaySchema
import shub39.momentum.core.backup.toProjectSchema
import shub39.momentum.data.database.DaysDao
import shub39.momentum.data.database.ProjectDao
import shub39.momentum.data.database.ProjectDatabase
import shub39.momentum.data.toDay
import shub39.momentum.data.toProject

@Single
class ExportRepoImpl(
    private val context: Context,
    private val projectDao: ProjectDao,
    private val daysDao: DaysDao,
) : ExportRepo {
    companion object {
        private const val TAG = "ExportRepo"
    }

    override suspend fun exportProjects(): ExportResult {
        // create temporary backup directory and zip file
        val backupDir = File(context.cacheDir, "backup")

        val time =
            Clock.System.now()
                .toLocalDateTime(TimeZone.UTC)
                .toString()
                .replace(":", "")
                .replace(" ", "")
        val zip = File(context.cacheDir, "backup_$time.zip")

        try {
            withContext(Dispatchers.IO) {
                if (!backupDir.exists()) backupDir.mkdirs()

                // prepare json
                val projects =
                    projectDao.getProjects().first().map { it.toProject().toProjectSchema() }
                val days = daysDao.getDays().first().map { it.toDay().toDaySchema() }

                val exportDetails =
                    ExportSchema(
                        schemaVersion = ProjectDatabase.SCHEMA_VERSION,
                        projects = projects,
                        days = days,
                    )

                File(backupDir, SCHEMA_FILE_NAME).writeText(Json.encodeToString(exportDetails))

                // prepare images
                days
                    .groupBy { it.projectId }
                    .forEach { (projectId, days) ->
                        if (days.isEmpty()) return@forEach
                        val dest = File(backupDir, projectId.toString())

                        days.forEach {
                            val source = it.image.toUri().toFile()
                            val destPicture = File(dest, "${it.date}_")

                            source.copyTo(destPicture)
                        }
                    }

                // zip files
                ZipOutputStream(BufferedOutputStream(FileOutputStream(zip))).use { out ->
                    backupDir.walkTopDown().forEach { file ->
                        if (file.isDirectory) return@forEach

                        val entryName =
                            file.relativeTo(backupDir).path.replace(File.separatorChar, '/')

                        val entry = ZipEntry(entryName)
                        out.putNextEntry(entry)

                        file.inputStream().use { input -> input.copyTo(out) }

                        out.closeEntry()
                    }
                }

                // select save location
                val file =
                    FileKit.openFileSaver(
                        suggestedName = "momentum_backup_$time",
                        defaultExtension = ".zip",
                    )
                file?.write(PlatformFile(zip))
            }

            return ExportResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "Error while exporting data", e)
            return ExportResult.Failure(e)
        } finally {
            backupDir.deleteRecursively()
            zip.deleteRecursively()
        }
    }
}
