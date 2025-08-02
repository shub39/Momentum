package shub39.momentum.core.data.datastore

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.domain.interfaces.SettingsPrefs

class SettingsPrefsImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsPrefs {

    companion object {
        private val seedColor = intPreferencesKey("seed_color")
        private val appTheme = stringPreferencesKey("app_theme")
        private val amoledPref = booleanPreferencesKey("with_amoled")
        private val paletteStyle = stringPreferencesKey("palette_style")
        private val materialTheme = booleanPreferencesKey("material_theme")
        private val onboardingDone = booleanPreferencesKey("onboarding_done")
        private val selectedFont = stringPreferencesKey("font")
        private val notificationPref = booleanPreferencesKey("notification_pref")
    }

    override fun getAppThemePrefFlow(): Flow<AppTheme> = dataStore.data
        .map { preferences ->
            val theme = preferences[appTheme] ?: AppTheme.SYSTEM.name
            AppTheme.valueOf(theme)
        }
    override suspend fun updateAppThemePref(pref: AppTheme) {
        dataStore.edit {
            it[appTheme] = pref.name
        }
    }

    override fun getSeedColorFlow(): Flow<Color> = dataStore.data
        .map { preferences -> Color(preferences[seedColor] ?: Color.Companion.White.toArgb()) }
    override suspend fun updateSeedColor(color: Color) {
        dataStore.edit { settings ->
            settings[seedColor] = color.toArgb()
        }
    }

    override fun getAmoledPrefFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[amoledPref] == true }
    override suspend fun updateAmoledPref(amoled: Boolean) {
        dataStore.edit { settings ->
            settings[amoledPref] = amoled
        }
    }

    override fun getPaletteStyle(): Flow<PaletteStyle> = dataStore.data
        .map { preferences ->
            PaletteStyle.valueOf(preferences[paletteStyle] ?: PaletteStyle.TonalSpot.name)
        }
    override suspend fun updatePaletteStyle(style: PaletteStyle) {
        dataStore.edit { settings ->
            settings[paletteStyle] = style.name
        }
    }

    override fun getOnboardingDoneFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[onboardingDone] == true }
    override suspend fun updateOnboardingDone(done: Boolean) {
        dataStore.edit { settings ->
            settings[onboardingDone] = done
        }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[materialTheme] == true }
    override suspend fun updateMaterialTheme(pref: Boolean) {
        dataStore.edit { settings ->
            settings[materialTheme] = pref
        }
    }

    override fun getFontFlow(): Flow<Fonts> = dataStore.data
        .map { prefs ->
            val font = prefs[selectedFont] ?: Fonts.FIGTREE.name
            Fonts.valueOf(font)
        }
    override suspend fun updateFonts(font: Fonts) {
        dataStore.edit { settings ->
            settings[selectedFont] = font.name
        }
    }

    override fun getNotificationPrefFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[notificationPref] == true }

    override suspend fun updateNotificationPref(pref: Boolean) {
        dataStore.edit { settings ->
            settings[notificationPref] = pref
        }
    }
}