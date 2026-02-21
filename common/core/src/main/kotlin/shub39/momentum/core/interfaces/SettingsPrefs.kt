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
