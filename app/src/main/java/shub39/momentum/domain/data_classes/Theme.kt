package shub39.momentum.domain.data_classes

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import shub39.momentum.domain.enums.AppTheme
import shub39.momentum.domain.enums.Fonts

data class Theme(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isAmoled: Boolean = false,
    val paletteStyle: PaletteStyle = PaletteStyle.Expressive,
    val isMaterialYou: Boolean = false,
    val seedColor: Color = Color.Blue,
    val font: Fonts = Fonts.FIGTREE
)