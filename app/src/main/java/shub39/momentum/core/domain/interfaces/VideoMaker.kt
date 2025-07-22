package shub39.momentum.core.domain.interfaces

import shub39.momentum.core.domain.data_classes.Day

interface VideoMaker {
    fun createMontage(days: List<Day>)
}