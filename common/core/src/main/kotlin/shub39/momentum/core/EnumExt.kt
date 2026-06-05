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
        FULL -> FULL
        LONG -> LONG
        MEDIUM -> MEDIUM
        SHORT -> SHORT
    }
}

fun Fonts.toFontRes(): Int? {
    return when (this) {
        INTER -> R.font.inter
        POPPINS -> R.font.poppins
        MANROPE -> R.font.manrope
        MONTSERRAT -> R.font.montserrat
        FIGTREE -> R.font.figtree
        QUICKSAND -> R.font.quicksand
        GOOGLE_SANS -> R.font.google_sans_flex
        SYSTEM_DEFAULT -> null
    }
}

fun VideoQuality.toDimensions(): Pair<Int, Int> {
    return when (this) {
        SMALL -> Pair(768, 1024)
        HD -> Pair(1080, 1440)
    }
}
