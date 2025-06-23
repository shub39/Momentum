package shub39.momentum.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProjectEntity::class,
        DayEntity::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class ProjectDatabase : RoomDatabase() {
    abstract val projectDao: ProjectDao
    abstract val daysDao: DaysDao

    companion object {
        const val DATABASE_NAME = "project_db"
    }
}