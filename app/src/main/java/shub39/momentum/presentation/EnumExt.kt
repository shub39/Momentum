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
package shub39.momentum.presentation

import shub39.momentum.R
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle

fun AppTheme.toStringRes(): Int {
    return when (this) {
        LIGHT -> R.string.theme_light
        DARK -> R.string.theme_dark
        SYSTEM -> R.string.theme_system
    }
}

fun Fonts.toDisplayString(): String {
    return when (this) {
        INTER -> "Inter"
        POPPINS -> "Poppins"
        MANROPE -> "Manrope"
        MONTSERRAT -> "Montserrat"
        FIGTREE -> "Figtree"
        QUICKSAND -> "Quicksand"
        GOOGLE_SANS -> "Google Sans"
        SYSTEM_DEFAULT -> "System Default"
    }
}

fun PaletteStyle.toMPaletteStyle(): com.materialkolor.PaletteStyle {
    return when (this) {
        TONALSPOT -> TonalSpot
        NEUTRAL -> Neutral
        VIBRANT -> Vibrant
        EXPRESSIVE -> Expressive
        RAINBOW -> Rainbow
        FRUITSALAD -> FruitSalad
        MONOCHROME -> Monochrome
        FIDELITY -> Fidelity
        CONTENT -> Content
    }
}
