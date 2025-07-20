package shub39.momentum.core.domain.enums

import shub39.momentum.R

enum class Fonts(
    val fontRes: Int,
    val displayName: String
) {
    INTER(R.font.inter, "Inter"),
    POPPINS(R.font.poppins, "Poppins"),
    MANROPE(R.font.manrope, "Manrope"),
    MONTSERRAT(R.font.montserrat, "Montserrat"),
    FIGTREE(R.font.figtree, "Figtree")
}