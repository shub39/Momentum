package shub39.momentum.core.domain.interfaces

import kotlinx.coroutines.flow.Flow
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.MontageConfig

interface MontageMaker {
    suspend fun createMontageFlow(
        days: List<Day>,
        config: MontageConfig
    ): Flow<MontageState>
}