package shub39.momentum.core.domain.interfaces

import kotlinx.coroutines.flow.Flow
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.MontageConfig
import java.io.File

interface MontageMaker {
    suspend fun createMontageFlow(
        days: List<Day>,
        file: File,
        config: MontageConfig
    ): Flow<MontageState>
}