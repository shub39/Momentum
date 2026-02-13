package shub39.momentum.domain.enums

import shub39.momentum.R

enum class Fonts(
    val fontRes: Int?,
    val displayName: String
) {
    INTER(R.font.inter, "Inter"),
    POPPINS(R.font.poppins, "Poppins"),
    MANROPE(R.font.manrope, "Manrope"),
    MONTSERRAT(R.font.montserrat, "Montserrat"),
    FIGTREE(R.font.figtree, "Figtree"),
    QUICKSAND(R.font.quicksand, "Quicksand"),
    GOOGLE_SANS(R.font.google_sans, "Google Sans"),
    SYSTEM_DEFAULT(null, "System Default")
}