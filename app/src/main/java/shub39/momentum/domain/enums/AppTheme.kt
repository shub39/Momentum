package shub39.momentum.domain.enums

import shub39.momentum.R

enum class AppTheme(
    val stringRes: Int
) {
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    SYSTEM(R.string.theme_system)
}