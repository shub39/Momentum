package shub39.momentum.home

import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.enums.SortOrder

data class HomeState(
    val searchQuery: String = "",
    val searchedProjects: List<Project> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DATE_CREATED_ASC,
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val completedProjects: List<Project> = emptyList(),

    val selectedProject: Project? = null
)