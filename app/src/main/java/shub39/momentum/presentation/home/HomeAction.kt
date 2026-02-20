package shub39.momentum.presentation.home

import shub39.momentum.core.data_classes.Project

sealed interface HomeAction {
    data class OnChangeProject(val project: Project) : HomeAction
    data class OnAddProject(
        val title: String,
        val description: String
    ) : HomeAction
}