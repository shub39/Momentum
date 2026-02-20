package shub39.momentum.core

import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.VideoQuality
import java.time.format.FormatStyle

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

fun VideoQuality.toDimensions(): Pair<Int, Int> {
    return when (this) {
        VideoQuality.SMALL -> Pair(768, 1024)
        VideoQuality.HD -> Pair(1080, 1440)
    }
}