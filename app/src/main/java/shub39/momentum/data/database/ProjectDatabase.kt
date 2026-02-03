package shub39.momentum.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ProjectEntity::class,
        DayEntity::class
    ],
    version = ProjectDatabase.SCHEMA_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)
abstract class ProjectDatabase : RoomDatabase() {
    abstract val projectDao: ProjectDao
    abstract val daysDao: DaysDao

    companion object {
        const val DATABASE_NAME = "project_db"
        const val SCHEMA_VERSION = 2
    }
}