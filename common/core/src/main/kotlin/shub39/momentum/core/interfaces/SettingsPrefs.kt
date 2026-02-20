package shub39.momentum.core.interfaces

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle

interface SettingsPrefs {
    suspend fun resetAppTheme()

    fun getAppThemePrefFlow(): Flow<AppTheme>
    suspend fun updateAppThemePref(pref: AppTheme)

    fun getSeedColorFlow(): Flow<Color>
    suspend fun updateSeedColor(color: Color)

    fun getAmoledPrefFlow(): Flow<Boolean>
    suspend fun updateAmoledPref(amoled: Boolean)

    fun getPaletteStyle(): Flow<PaletteStyle>
    suspend fun updatePaletteStyle(style: PaletteStyle)

    fun getOnboardingDoneFlow(): Flow<Boolean>
    suspend fun updateOnboardingDone(done: Boolean)

    fun getMaterialYouFlow(): Flow<Boolean>
    suspend fun updateMaterialTheme(pref: Boolean)

    fun getFontFlow(): Flow<Fonts>
    suspend fun updateFonts(font: Fonts)

    fun getLastChangelogShown(): Flow<String>
    suspend fun updateLastChangelogShown(version: String)
}