package shub39.momentum.data.backup

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.toAndroidUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import shub39.momentum.core.backup.ExportSchema
import shub39.momentum.core.backup.ImportExceptionType
import shub39.momentum.core.backup.ImportRepo
import shub39.momentum.core.backup.ImportResult
import shub39.momentum.core.backup.SCHEMA_FILE_NAME
import shub39.momentum.core.backup.toDay
import shub39.momentum.core.backup.toProject
import shub39.momentum.data.database.DaysDao
import shub39.momentum.data.database.ProjectDao
import shub39.momentum.data.toDayEntity
import shub39.momentum.data.toEntity
import java.io.BufferedInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

@Single
class ImportRepoImpl(
    private val context: Context,
    private val projectDao: ProjectDao,
    private val daysDao: DaysDao
) : ImportRepo {
    companion object {
        private const val TAG = "ImportRepo"
    }

    override suspend fun restoreData(): ImportResult {
        val unzipDir = File(context.cacheDir, "import")

        try {
            // ======= select file
            Log.d(TAG, "Selecting File")
            val file = FileKit.openFilePicker(
                type = FileKitType.File("zip")
            )
            if (file == null) return ImportResult.Failure(ImportExceptionType.NoFileSelected)
            Log.d(TAG, "File Selected")

            // ========= unzip
            Log.d(TAG, "Unzipping File")
            if (!unzipDir.exists()) unzipDir.mkdirs()

            withContext(Dispatchers.IO) {
                val inputStream = context.contentResolver.openInputStream(file.toAndroidUri())
                    ?: throw FileNotFoundException("Could not open file")
                ZipInputStream(
                    BufferedInputStream(inputStream)
                ).use { zipIn ->
                    var entry = zipIn.nextEntry

                    while (entry != null) {
                        val outFile = File(unzipDir, entry.name)

                        if (outFile.isDirectory) {
                            outFile.mkdirs()
                        } else {
                            outFile.parentFile?.mkdirs()

                            FileOutputStream(outFile).use { out ->
                                zipIn.copyTo(out)
                            }
                        }

                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            }
            Log.d(TAG, "File Unzipped")

            // ========== import data
            Log.d(TAG, "Importing Data")
            val schemaFile = File(unzipDir, SCHEMA_FILE_NAME)
            if (!schemaFile.exists()) return ImportResult.Failure(ImportExceptionType.InvalidFile)

            withContext(Dispatchers.IO) {
                val schema = Json.decodeFromString<ExportSchema>(schemaFile.readText())

                schema.projects.forEach {
                    projectDao.upsertProject(it.toProject().toEntity())
                }

                schema.days.groupBy { it.projectId }.forEach { (id, schemas) ->
                    val srcDir = File(unzipDir, id.toString())
                    val destDir = File(context.filesDir, id.toString())

                    if (!destDir.exists()) destDir.mkdirs()

                    schemas.forEach { daySchema ->
                        val fileName = "${daySchema.date}_"
                        val srcFile = File(srcDir, fileName)

                        if (srcFile.exists()) {
                            val destFile = File(destDir, fileName)
                            srcFile.copyTo(destFile, overwrite = true)

                            val newSchema = daySchema.copy(image = destFile.toUri().toString())
                            daysDao.upsertDay(newSchema.toDay().toDayEntity())
                        }
                    }
                }
            }

            return ImportResult.Success
        } catch (e: SerializationException) {
            Log.e(TAG, "Invalid Schema found", e)
            return ImportResult.Failure(ImportExceptionType.InvalidSchema)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Invalid File", e)
            return ImportResult.Failure(ImportExceptionType.InvalidFile)
        } catch (e: Exception) {
            Log.e(TAG, "Other Error", e)
            return ImportResult.Failure(ImportExceptionType.Other(e))
        } finally {
            unzipDir.deleteRecursively()
        }
    }
}
