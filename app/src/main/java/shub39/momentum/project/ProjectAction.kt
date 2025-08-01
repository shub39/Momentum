package shub39.momentum.project

import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project

sealed interface ProjectAction {
    data class OnUpdateProject(val project: Project) : ProjectAction
    data class OnDeleteProject(val project: Project) : ProjectAction
    data class OnUpsertDay(val day: Day) : ProjectAction
    data class OnDeleteDay(val day: Day) : ProjectAction

    data object OnCreateMontage : ProjectAction
}