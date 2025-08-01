package shub39.momentum.core.domain.interfaces

import java.io.File

sealed interface MontageResult {
    data class Success(val file: File) : MontageResult

    data class Error(
        val message: String,
        val exception: Exception
    ) : MontageResult
}