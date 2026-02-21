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
package shub39.momentum.presentation.settings

import androidx.compose.ui.graphics.Color
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle

sealed interface SettingsAction {
    data class OnSeedColorChange(val color: Color) : SettingsAction

    data class OnThemeSwitch(val appTheme: AppTheme) : SettingsAction

    data class OnAmoledSwitch(val amoled: Boolean) : SettingsAction

    data class OnPaletteChange(val style: PaletteStyle) : SettingsAction

    data class OnMaterialThemeToggle(val pref: Boolean) : SettingsAction

    data class OnFontChange(val fonts: Fonts) : SettingsAction

    data class OnOnboardingToggle(val done: Boolean) : SettingsAction
}
