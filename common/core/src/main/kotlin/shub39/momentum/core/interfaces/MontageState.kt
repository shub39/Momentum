package shub39.momentum.core.interfaces

import shub39.momentum.core.data_classes.MontageConfig
import java.io.File

sealed interface MontageState {
    data class Processing(
        val progress: Float = 0f,
        val status: String = "Zzz"
    ) : MontageState

    data class Success(
        val file: File,
        val config: MontageConfig
    ) : MontageState

    data class Error(
        val message: String,
        val exception: Exception
    ) : MontageState
}