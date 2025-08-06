package shub39.momentum.core.domain.interfaces

import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.MontageConfig
import java.io.File

interface MontageMaker {
    suspend fun createMontage(
        days: List<Day>,
        file: File,
        montageConfig: MontageConfig
    ): MontageState
}