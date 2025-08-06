package shub39.momentum.home

import shub39.momentum.core.domain.data_classes.ProjectListData

data class HomeState(
    val sendNotifications: Boolean = false,
    val projects: List<ProjectListData> = emptyList(),
)