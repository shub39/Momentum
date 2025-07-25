package shub39.momentum.core.domain.interfaces

interface MontageResult {
    data object Success : MontageResult
    data class Error(val error: String) : MontageResult
}