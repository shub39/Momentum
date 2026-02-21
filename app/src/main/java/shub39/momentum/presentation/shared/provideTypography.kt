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
package shub39.momentum.presentation.shared

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.toFontRes

@Composable
fun provideTypography(scale: Float = 1f, font: Fonts = Fonts.FIGTREE): Typography {
    val selectedFont = font.toFontRes()?.let { FontFamily(Font(it)) } ?: FontFamily.Default

    return Typography(
        displayLarge =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Black,
                fontSize = 57.sp * scale,
                lineHeight = 64.sp * scale,
                letterSpacing = -(0.25).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Black,
                fontSize = 45.sp * scale,
                lineHeight = 52.sp * scale,
            ),
        displaySmall =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Black,
                fontSize = 36.sp * scale,
                lineHeight = 44.sp * scale,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp * scale,
                lineHeight = 40.sp * scale,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp * scale,
                lineHeight = 36.sp * scale,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp * scale,
                lineHeight = 32.sp * scale,
            ),
        titleLarge =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp * scale,
                lineHeight = 28.sp * scale,
            ),
        titleMedium =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp * scale,
                lineHeight = 24.sp * scale,
                letterSpacing = 0.15.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp * scale,
                lineHeight = 20.sp * scale,
                letterSpacing = 0.1.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp * scale,
                lineHeight = 16.sp * scale,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp * scale,
                lineHeight = 14.sp * scale,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp * scale,
                lineHeight = 12.sp * scale,
                letterSpacing = 0.5.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp * scale,
                lineHeight = 24.sp * scale,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp * scale,
                lineHeight = 20.sp * scale,
                letterSpacing = 0.25.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = selectedFont,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp * scale,
                lineHeight = 16.sp * scale,
                letterSpacing = 0.4.sp,
            ),
    )
}
