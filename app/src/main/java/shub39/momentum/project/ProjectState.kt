package shub39.momentum.project

import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.interfaces.MontageResult

sealed class ProjectState {
    data object Loading : ProjectState()

    data class Loaded(
        val project: Project,
        val days: List<Day> = emptyList(),
        val montage: MontageResult? = null
    ) : ProjectState()
}