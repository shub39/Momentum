package shub39.momentum.data.backup

import android.content.Context
import org.koin.core.annotation.Single
import shub39.momentum.core.backup.ImportRepo
import shub39.momentum.core.backup.ImportResult
import shub39.momentum.data.database.DaysDao
import shub39.momentum.data.database.ProjectDao
import java.io.File

@Single
class ImportRepoImpl(
    private val context: Context,
    private val projectDao: ProjectDao,
    private val daysDao: DaysDao
): ImportRepo {
    companion object {
        private const val TAG = "ImportRepo"
    }

    override suspend fun restoreData(): ImportResult {
        TODO()
    }
}