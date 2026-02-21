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
package shub39.momentum.di

import android.content.Context
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import shub39.facedetection.FaceDetectorImpl
import shub39.momentum.core.interfaces.FaceDetector
import shub39.momentum.core.interfaces.MontageConfigPrefs
import shub39.momentum.core.interfaces.MontageMaker
import shub39.momentum.core.interfaces.SettingsPrefs
import shub39.momentum.data.database.DaysDao
import shub39.momentum.data.database.ProjectDBFactory
import shub39.momentum.data.database.ProjectDao
import shub39.momentum.data.database.ProjectDatabase
import shub39.momentum.data.datastore.DatastoreFactory
import shub39.montage.MontageMakerImpl

@Module
@ComponentScan("shub39.momentum")
class AppModule {
    @Single
    fun provideAppDb(dbFactory: ProjectDBFactory): ProjectDatabase {
        return dbFactory.create().fallbackToDestructiveMigration(true).build()
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

    @Single
    fun getMontageMaker(context: Context): MontageMaker = MontageMakerImpl(context)

    @Single
    fun getFaceDetector(context: Context): FaceDetector = FaceDetectorImpl(context)
}
