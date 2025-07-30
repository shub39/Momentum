package shub39.momentum.project

import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import java.time.LocalDate

data class ProjectState(
    val project: Project? = null,
    val days: List<Day> = emptyList(),
    val dates: List<LocalDate> = emptyList(),
    val montageProgress: Float? = null,
)
