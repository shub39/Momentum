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
package shub39.momentum.core

import java.time.format.FormatStyle
import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.VideoQuality

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
