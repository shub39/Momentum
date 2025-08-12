package shub39.momentum.core.domain.enums

import java.time.format.FormatStyle

enum class DateStyle {
    FULL,
    LONG,
    MEDIUM,
    SHORT;

    companion object {
        fun DateStyle.toFormatStyle(): FormatStyle {
            return when (this) {
                FULL -> FormatStyle.FULL
                LONG -> FormatStyle.LONG
                MEDIUM -> FormatStyle.MEDIUM
                SHORT -> FormatStyle.SHORT
            }
        }
    }
}