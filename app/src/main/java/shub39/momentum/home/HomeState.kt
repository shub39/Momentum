package shub39.momentum.home

import shub39.momentum.core.domain.data_classes.Project

data class HomeState(
    val sendNotifications: Boolean = false,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val selectedProject: Project? = null
)