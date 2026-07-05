package shub39.momentum.core.backup

interface ImportRepo {
    suspend fun restoreData(): ImportResult
}

sealed class ImportResult {
    data object Success : ImportResult()

    data class Failure(val exception: Exception) : ImportResult()
}

enum class ImportState {
    IDLE,
    IMPORTING,
    IMPORTED,
    FAILURE,
}