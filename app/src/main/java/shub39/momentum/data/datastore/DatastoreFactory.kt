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
package shub39.momentum.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import shub39.momentum.core.interfaces.MontageConfigPrefs
import shub39.momentum.core.interfaces.SettingsPrefs

@Single
class DatastoreFactory(private val context: Context) {
    fun settingsPreferences(): SettingsPrefs = SettingsPrefsImpl(settingsDatastoreFile())

    fun montageConfig(): MontageConfigPrefs = MontageConfigPrefsImpl(montageConfigFile())

    private fun settingsDatastoreFile(): DataStore<Preferences> =
        createDataStore(producePath = { context.filesDir.resolve(SETTINGS_DATASTORE).absolutePath })

    private fun montageConfigFile(): DataStore<Preferences> =
        createDataStore(producePath = { context.filesDir.resolve(MONTAGE_CONFIG).absolutePath })

    companion object {
        const val SETTINGS_DATASTORE = "settings.preferences_pb"
        const val MONTAGE_CONFIG = "montage.preferences_pb"
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
