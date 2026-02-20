package shub39.momentum.core.data_classes

import androidx.compose.ui.graphics.Color
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle

data class Theme(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isAmoled: Boolean = false,
    val paletteStyle: PaletteStyle = PaletteStyle.EXPRESSIVE,
    val isMaterialYou: Boolean = false,
    val seedColor: Color = Color.Blue,
    val font: Fonts = Fonts.FIGTREE
)