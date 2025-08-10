package shub39.momentum.project

import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.MontageConfig
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.interfaces.MontageState

data class ProjectState(
    val project: Project? = null,
    val days: List<Day> = emptyList(),
    val montage: MontageState = MontageState.Processing,
    val montageConfig: MontageConfig = MontageConfig(),
    val selectedDate: Long? = null,
)