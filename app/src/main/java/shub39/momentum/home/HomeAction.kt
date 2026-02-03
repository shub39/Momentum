package shub39.momentum.home

import shub39.momentum.domain.data_classes.Project

sealed interface HomeAction {
    data object OnShowPaywall : HomeAction
    data class OnChangeProject(val project: Project) : HomeAction
    data class OnAddProject(
        val title: String,
        val description: String
    ) : HomeAction
}