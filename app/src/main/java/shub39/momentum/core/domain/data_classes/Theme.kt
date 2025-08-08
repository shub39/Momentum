package shub39.momentum.core.domain.data_classes

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts

data class Theme(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isAmoled: Boolean = false,
    val paletteStyle: PaletteStyle = PaletteStyle.Expressive,
    val isMaterialYou: Boolean = false,
    val seedColor: Color = Color.Blue,
    val font: Fonts = Fonts.FIGTREE
)