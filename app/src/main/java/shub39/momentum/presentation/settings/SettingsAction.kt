package shub39.momentum.presentation.settings

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import shub39.momentum.domain.enums.AppTheme
import shub39.momentum.domain.enums.Fonts

sealed interface SettingsAction {
    data class OnSeedColorChange(val color: Color): SettingsAction
    data class OnThemeSwitch(val appTheme: AppTheme): SettingsAction
    data class OnAmoledSwitch(val amoled: Boolean): SettingsAction
    data class OnPaletteChange(val style: PaletteStyle): SettingsAction
    data class OnMaterialThemeToggle(val pref: Boolean): SettingsAction
    data class OnFontChange(val fonts: Fonts): SettingsAction
    data class OnOnboardingToggle(val done: Boolean): SettingsAction
}