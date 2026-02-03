package shub39.momentum.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.annotation.Single

@Single
class ProjectDBFactory(
    private val context: Context
) {
    fun create(): RoomDatabase.Builder<ProjectDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(ProjectDatabase.DATABASE_NAME)

        return Room.databaseBuilder(appContext, dbFile.absolutePath)
    }
}