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
        AppTheme.LIGHT -> R.string.theme_light
        AppTheme.DARK -> R.string.theme_dark
        AppTheme.SYSTEM -> R.string.theme_system
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
