package shub39.momentum.core.backup

interface ImportRepo {
    suspend fun restoreData(): ImportResult
}

sealed class ImportResult {
    data object Success : ImportResult()

    data class Failure(val exception: ImportExceptionType) : ImportResult()
}

sealed interface ImportExceptionType {
    data object NoFileSelected: ImportExceptionType
    data object InvalidSchema: ImportExceptionType
    data object InvalidFile: ImportExceptionType
    data class Other(val e: Exception): ImportExceptionType
}

enum class ImportState {
    IDLE,
    IMPORTING,
    IMPORTED,
    FAILURE,
}