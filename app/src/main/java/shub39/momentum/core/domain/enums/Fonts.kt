package shub39.momentum.core.domain.enums

import androidx.annotation.FontRes
import shub39.momentum.R

enum class Fonts(
    @FontRes val fontRes: Int,
) {
    INTER(R.font.inter),
    POPPINS(R.font.poppins),
    MANROPE(R.font.manrope),
    MONTSERRAT(R.font.montserrat)
}