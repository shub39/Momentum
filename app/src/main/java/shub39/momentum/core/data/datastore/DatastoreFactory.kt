package shub39.momentum.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

class DatastoreFactory(
    private val context: Context
) {
    fun getSettingsDatastore(): DataStore<Preferences> = createDataStore (
        producePath = { context.filesDir.resolve(SETTINGS_DATASTORE).absolutePath }
    )

    companion object {
        const val SETTINGS_DATASTORE = "settings.preferences_pb"
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })