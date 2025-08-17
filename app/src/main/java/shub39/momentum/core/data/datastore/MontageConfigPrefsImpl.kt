package shub39.momentum.core.data.datastore

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shub39.momentum.core.domain.enums.DateStyle
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.domain.enums.VideoQuality
import shub39.momentum.core.domain.interfaces.MontageConfigPrefs

class MontageConfigPrefsImpl(
    private val dataStore: DataStore<Preferences>
) : MontageConfigPrefs {

    override suspend fun resetPrefs() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        private val fpiKey = intPreferencesKey("montage_fpi")
        private val fpsKey = floatPreferencesKey("montage_fps")
        private val videoQualityKey = stringPreferencesKey("montage_video_quality")
        private val backgroundColorKey = intPreferencesKey("montage_background_color")
        private val waterMarkKey = booleanPreferencesKey("montage_watermark")
        private val showDateKey = booleanPreferencesKey("montage_show_date")
        private val showMessageKey = booleanPreferencesKey("montage_show_message")
        private val fontKey = stringPreferencesKey("montage_font")
        private val dateStyleKey = stringPreferencesKey("montage_date_style")
    }

    override fun getFpiFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[fpiKey] ?: 1 }

    override suspend fun setFpi(fpi: Int) {
        dataStore.edit { it[fpiKey] = fpi }
    }

    override fun getFpsFlow(): Flow<Float> = dataStore.data
        .map { preferences -> preferences[fpsKey] ?: 3f }

    override suspend fun setFps(fps: Float) {
        dataStore.edit { it[fpsKey] = fps }
    }

    override fun getVideoQualityFlow(): Flow<VideoQuality> = dataStore.data
        .map { preferences ->
            VideoQuality.valueOf(preferences[videoQualityKey] ?: VideoQuality.SMALL.name)
        }

    override suspend fun setVideoQuality(quality: VideoQuality) {
        dataStore.edit { it[videoQualityKey] = quality.name }
    }

    override fun getBackgroundColorFlow(): Flow<Color> = dataStore.data
        .map { preferences ->
            Color(preferences[backgroundColorKey] ?: Color.Black.toArgb())
        }

    override suspend fun setBackgroundColor(color: Color) {
        dataStore.edit { it[backgroundColorKey] = color.toArgb() }
    }

    override fun getWaterMarkFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[waterMarkKey] ?: true }

    override suspend fun setWaterMark(show: Boolean) {
        dataStore.edit { it[waterMarkKey] = show }
    }

    override fun getShowDateFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[showDateKey] ?: true }

    override suspend fun setShowDate(show: Boolean) {
        dataStore.edit { it[showDateKey] = show }
    }

    override fun getShowMessageFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[showMessageKey] ?: true }

    override suspend fun setShowMessage(show: Boolean) {
        dataStore.edit { it[showMessageKey] = show }
    }

    override fun getFontFlow(): Flow<Fonts> = dataStore.data
        .map { preferences ->
            Fonts.valueOf(preferences[fontKey] ?: Fonts.FIGTREE.name)
        }

    override suspend fun setFont(font: Fonts) {
        dataStore.edit { it[fontKey] = font.name }
    }

    override fun getDateStyleFlow(): Flow<DateStyle> = dataStore.data
        .map { preferences ->
            DateStyle.valueOf(preferences[dateStyleKey] ?: DateStyle.FULL.name)
        }

    override suspend fun setDateStyle(style: DateStyle) {
        dataStore.edit { it[dateStyleKey] = style.name }
    }
}
