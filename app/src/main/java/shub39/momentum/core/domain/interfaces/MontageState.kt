package shub39.momentum.core.domain.interfaces

import shub39.momentum.core.domain.data_classes.MontageConfig
import java.io.File

sealed interface MontageState {
    data object Processing : MontageState
    data class Success(
        val file: File,
        val config: MontageConfig
    ) : MontageState
    data class Error(
        val message: String,
        val exception: Exception
    ) : MontageState
}