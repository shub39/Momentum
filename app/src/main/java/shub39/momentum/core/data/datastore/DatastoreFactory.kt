package shub39.momentum.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import shub39.momentum.core.domain.interfaces.MontageConfigPrefs
import shub39.momentum.core.domain.interfaces.SettingsPrefs

@Single
class DatastoreFactory(
    private val context: Context
) {
    fun settingsPreferences(): SettingsPrefs = SettingsPrefsImpl(settingsDatastoreFile())

    fun montageConfig(): MontageConfigPrefs = MontageConfigPrefsImpl(montageConfigFile())

    private fun settingsDatastoreFile(): DataStore<Preferences> = createDataStore(
        producePath = { context.filesDir.resolve(SETTINGS_DATASTORE).absolutePath }
    )

    private fun montageConfigFile(): DataStore<Preferences> = createDataStore(
        producePath = { context.filesDir.resolve(MONTAGE_CONFIG).absolutePath }
    )

    companion object {
        const val SETTINGS_DATASTORE = "settings.preferences_pb"
        const val MONTAGE_CONFIG = "montage.preferences_pb"
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })