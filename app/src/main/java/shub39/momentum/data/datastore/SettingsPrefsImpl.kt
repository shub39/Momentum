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

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle
import shub39.momentum.core.interfaces.SettingsPrefs

class SettingsPrefsImpl(private val dataStore: DataStore<Preferences>) : SettingsPrefs {

    override suspend fun resetAppTheme() {
        dataStore.edit { prefs ->
            prefs.remove(seedColor)
            prefs.remove(appTheme)
            prefs.remove(amoledPref)
            prefs.remove(paletteStyle)
            prefs.remove(materialTheme)
        }
    }

    companion object {
        private val seedColor = intPreferencesKey("seed_color")
        private val appTheme = stringPreferencesKey("app_theme")
        private val amoledPref = booleanPreferencesKey("with_amoled")
        private val paletteStyle = stringPreferencesKey("palette_style")
        private val materialTheme = booleanPreferencesKey("material_theme")
        private val onboardingDone = booleanPreferencesKey("onboarding_done")
        private val selectedFont = stringPreferencesKey("font")
        private val lastChangelogShownKey = stringPreferencesKey("last_changelog_shown")
    }

    override fun getAppThemePrefFlow(): Flow<AppTheme> =
        dataStore.data.map { preferences ->
            val theme = preferences[appTheme] ?: AppTheme.SYSTEM.name
            AppTheme.valueOf(theme)
        }

    override suspend fun updateAppThemePref(pref: AppTheme) {
        dataStore.edit { it[appTheme] = pref.name }
    }

    override fun getSeedColorFlow(): Flow<Color> =
        dataStore.data.map { preferences -> Color(preferences[seedColor] ?: Color.White.toArgb()) }

    override suspend fun updateSeedColor(color: Color) {
        dataStore.edit { settings -> settings[seedColor] = color.toArgb() }
    }

    override fun getAmoledPrefFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[amoledPref] == true }

    override suspend fun updateAmoledPref(amoled: Boolean) {
        dataStore.edit { settings -> settings[amoledPref] = amoled }
    }

    override fun getPaletteStyle(): Flow<PaletteStyle> =
        dataStore.data.map { preferences ->
            try {
                PaletteStyle.valueOf(preferences[paletteStyle] ?: PaletteStyle.TONALSPOT.name)
            } catch (e: Exception) {
                Log.wtf("OtherPreferencesImpl", "Error getting palette style: ${e.message}")
                PaletteStyle.TONALSPOT
            }
        }

    override suspend fun updatePaletteStyle(style: PaletteStyle) {
        dataStore.edit { settings -> settings[paletteStyle] = style.name }
    }

    override fun getOnboardingDoneFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[onboardingDone] == true }

    override suspend fun updateOnboardingDone(done: Boolean) {
        dataStore.edit { settings -> settings[onboardingDone] = done }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[materialTheme] == true }

    override suspend fun updateMaterialTheme(pref: Boolean) {
        dataStore.edit { settings -> settings[materialTheme] = pref }
    }

    override fun getFontFlow(): Flow<Fonts> =
        dataStore.data.map { prefs ->
            val font = prefs[selectedFont] ?: Fonts.FIGTREE.name
            Fonts.valueOf(font)
        }

    override suspend fun updateFonts(font: Fonts) {
        dataStore.edit { settings -> settings[selectedFont] = font.name }
    }

    override fun getLastChangelogShown(): Flow<String> =
        dataStore.data.map { prefs -> prefs[lastChangelogShownKey] ?: "" }

    override suspend fun updateLastChangelogShown(version: String) {
        dataStore.edit { settings -> settings[lastChangelogShownKey] = version }
    }
}
