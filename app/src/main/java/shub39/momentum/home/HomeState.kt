package shub39.momentum.home

import shub39.momentum.core.domain.data_classes.ProjectListData

sealed class HomeState {
    data object Loading : HomeState()

    data class ProjectList(
        val sendNotifications: Boolean = false,
        val projects: List<ProjectListData> = emptyList(),
    ) : HomeState()
}