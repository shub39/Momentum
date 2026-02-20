package shub39.momentum.presentation.settings

import androidx.compose.ui.graphics.Color
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle

sealed interface SettingsAction {
    data class OnSeedColorChange(val color: Color): SettingsAction
    data class OnThemeSwitch(val appTheme: AppTheme): SettingsAction
    data class OnAmoledSwitch(val amoled: Boolean): SettingsAction
    data class OnPaletteChange(val style: PaletteStyle): SettingsAction
    data class OnMaterialThemeToggle(val pref: Boolean): SettingsAction
    data class OnFontChange(val fonts: Fonts): SettingsAction
    data class OnOnboardingToggle(val done: Boolean): SettingsAction
}