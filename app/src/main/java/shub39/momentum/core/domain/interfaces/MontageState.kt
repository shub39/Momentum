package shub39.momentum.core.domain.interfaces

import java.io.File

sealed interface MontageState {
    data object Processing : MontageState
    data class Success(val file: File) : MontageState
    data class Error(
        val message: String,
        val exception: Exception
    ) : MontageState
}