package shub39.momentum.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import shub39.momentum.core.interfaces.MontageConfigPrefs
import shub39.momentum.core.interfaces.SettingsPrefs
import shub39.momentum.data.database.DaysDao
import shub39.momentum.data.database.ProjectDBFactory
import shub39.momentum.data.database.ProjectDao
import shub39.momentum.data.database.ProjectDatabase
import shub39.momentum.data.datastore.DatastoreFactory

@Module
@ComponentScan("shub39.momentum")
class AppModule {
    @Single
    fun provideAppDb(
        dbFactory: ProjectDBFactory
    ): ProjectDatabase {
        return dbFactory
            .create()
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Single
    fun getProjectDao(database: ProjectDatabase): ProjectDao = database.projectDao

    @Single
    fun getDaysDao(database: ProjectDatabase): DaysDao = database.daysDao

    @Single
    fun getSettingsPrefs(datastoreFactory: DatastoreFactory): SettingsPrefs =
        datastoreFactory.settingsPreferences()

    @Single
    fun getMontageConfigPrefs(datastoreFactory: DatastoreFactory): MontageConfigPrefs =
        datastoreFactory.montageConfig()
}