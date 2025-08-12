package shub39.momentum.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import shub39.momentum.core.data.database.DaysDao
import shub39.momentum.core.data.database.ProjectDBFactory
import shub39.momentum.core.data.database.ProjectDao
import shub39.momentum.core.data.database.ProjectDatabase
import shub39.momentum.core.data.datastore.DatastoreFactory
import shub39.momentum.core.domain.interfaces.MontageConfigPrefs
import shub39.momentum.core.domain.interfaces.SettingsPrefs

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