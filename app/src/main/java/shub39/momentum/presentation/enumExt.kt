package shub39.momentum.presentation

import shub39.momentum.R
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle
import shub39.momentum.core.enums.VideoQuality
import shub39.momentum.core.enums.VideoQuality.HD
import shub39.momentum.core.enums.VideoQuality.SMALL
import java.time.format.FormatStyle

fun AppTheme.toStringRes(): Int {
    return when (this) {
        AppTheme.LIGHT -> R.string.theme_light
        AppTheme.DARK -> R.string.theme_dark
        AppTheme.SYSTEM -> R.string.theme_system
    }
}

fun DateStyle.toFormatStyle(): FormatStyle {
    return when (this) {
        DateStyle.FULL -> FormatStyle.FULL
        DateStyle.LONG -> FormatStyle.LONG
        DateStyle.MEDIUM -> FormatStyle.MEDIUM
        DateStyle.SHORT -> FormatStyle.SHORT
    }
}

fun Fonts.toFontRes(): Int? {
    return when (this) {
        Fonts.INTER -> R.font.inter
        Fonts.POPPINS -> R.font.poppins
        Fonts.MANROPE -> R.font.manrope
        Fonts.MONTSERRAT -> R.font.montserrat
        Fonts.FIGTREE -> R.font.figtree
        Fonts.QUICKSAND -> R.font.quicksand
        Fonts.GOOGLE_SANS -> R.font.google_sans
        Fonts.SYSTEM_DEFAULT -> null
    }
}

fun Fonts.toDisplayString(): String {
    return when (this) {
        Fonts.INTER -> "Inter"
        Fonts.POPPINS -> "Poppins"
        Fonts.MANROPE -> "Manrope"
        Fonts.MONTSERRAT -> "Montserrat"
        Fonts.FIGTREE -> "Figtree"
        Fonts.QUICKSAND -> "Quicksand"
        Fonts.GOOGLE_SANS -> "Google Sans"
        Fonts.SYSTEM_DEFAULT -> "System Default"
    }
}

fun VideoQuality.toDimensions(): Pair<Int, Int> {
    return when (this) {
        SMALL -> Pair(768, 1024)
        HD -> Pair(1080, 1440)
    }
}

fun PaletteStyle.toMPaletteStyle(): com.materialkolor.PaletteStyle {
    return when (this) {
        PaletteStyle.TONALSPOT -> com.materialkolor.PaletteStyle.TonalSpot
        PaletteStyle.NEUTRAL -> com.materialkolor.PaletteStyle.Neutral
        PaletteStyle.VIBRANT -> com.materialkolor.PaletteStyle.Vibrant
        PaletteStyle.EXPRESSIVE -> com.materialkolor.PaletteStyle.Expressive
        PaletteStyle.RAINBOW -> com.materialkolor.PaletteStyle.Rainbow
        PaletteStyle.FRUITSALAD -> com.materialkolor.PaletteStyle.FruitSalad
        PaletteStyle.MONOCHROME -> com.materialkolor.PaletteStyle.Monochrome
        PaletteStyle.FIDELITY -> com.materialkolor.PaletteStyle.Fidelity
        PaletteStyle.CONTENT -> com.materialkolor.PaletteStyle.Content
    }
}