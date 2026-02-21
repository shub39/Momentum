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
import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.VideoQuality
import shub39.momentum.core.interfaces.MontageConfigPrefs

class MontageConfigPrefsImpl(private val dataStore: DataStore<Preferences>) : MontageConfigPrefs {

    override suspend fun resetPrefs() {
        dataStore.edit { prefs -> prefs.clear() }
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
        private val stabilizeFacesKey = booleanPreferencesKey("stabilize_faces")
        private val censorKey = booleanPreferencesKey("censor_faces")
    }

    override fun getFpiFlow(): Flow<Int> =
        dataStore.data.map { preferences -> preferences[fpiKey] ?: 1 }

    override suspend fun setFpi(fpi: Int) {
        dataStore.edit { it[fpiKey] = fpi }
    }

    override fun getFpsFlow(): Flow<Float> =
        dataStore.data.map { preferences -> preferences[fpsKey] ?: 3f }

    override suspend fun setFps(fps: Float) {
        dataStore.edit { it[fpsKey] = fps }
    }

    override fun getVideoQualityFlow(): Flow<VideoQuality> =
        dataStore.data.map { preferences ->
            VideoQuality.valueOf(preferences[videoQualityKey] ?: VideoQuality.SMALL.name)
        }

    override suspend fun setVideoQuality(quality: VideoQuality) {
        dataStore.edit { it[videoQualityKey] = quality.name }
    }

    override fun getBackgroundColorFlow(): Flow<Color> =
        dataStore.data.map { preferences ->
            Color(preferences[backgroundColorKey] ?: Color.Black.toArgb())
        }

    override suspend fun setBackgroundColor(color: Color) {
        dataStore.edit { it[backgroundColorKey] = color.toArgb() }
    }

    override fun getWaterMarkFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[waterMarkKey] ?: true }

    override suspend fun setWaterMark(show: Boolean) {
        dataStore.edit { it[waterMarkKey] = show }
    }

    override fun getShowDateFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[showDateKey] ?: true }

    override suspend fun setShowDate(show: Boolean) {
        dataStore.edit { it[showDateKey] = show }
    }

    override fun getShowMessageFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[showMessageKey] ?: true }

    override suspend fun setShowMessage(show: Boolean) {
        dataStore.edit { it[showMessageKey] = show }
    }

    override fun getFontFlow(): Flow<Fonts> =
        dataStore.data.map { preferences ->
            Fonts.valueOf(preferences[fontKey] ?: Fonts.FIGTREE.name)
        }

    override suspend fun setFont(font: Fonts) {
        dataStore.edit { it[fontKey] = font.name }
    }

    override fun getDateStyleFlow(): Flow<DateStyle> =
        dataStore.data.map { preferences ->
            DateStyle.valueOf(preferences[dateStyleKey] ?: DateStyle.FULL.name)
        }

    override suspend fun setDateStyle(style: DateStyle) {
        dataStore.edit { it[dateStyleKey] = style.name }
    }

    override fun getStabilizeFacesFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[stabilizeFacesKey] ?: false }

    override suspend fun setStabilizeFaces(pref: Boolean) {
        dataStore.edit { it[stabilizeFacesKey] = pref }
    }

    override fun getCensorPref(): Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[censorKey] ?: false }

    override suspend fun setCensorPref(pref: Boolean) {
        dataStore.edit { it[censorKey] = pref }
    }
}
