package shub39.momentum.core.domain.enums

import androidx.annotation.FontRes
import shub39.momentum.R

enum class Fonts(
    @FontRes val fontRes: Int,
    val displayName: String
) {
    INTER(R.font.inter, "Inter"),
    POPPINS(R.font.poppins, "Poppins"),
    MANROPE(R.font.manrope, "Manrope"),
    MONTSERRAT(R.font.montserrat, "Montserrat")
}