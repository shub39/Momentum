package shub39.momentum.presentation.project

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageConfig
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.interfaces.MontageState

sealed interface ScanState {
    data object Idle : ScanState
    data class Processing(val progress: Float) : ScanState
    data object Done : ScanState
}

@Stable
@Immutable
data class ProjectState(
    val project: Project? = null,
    val days: List<Day> = emptyList(),
    val montage: MontageState = MontageState.Processing(),
    val montageConfig: MontageConfig = MontageConfig(),
    val scanState: ScanState = ScanState.Idle
)