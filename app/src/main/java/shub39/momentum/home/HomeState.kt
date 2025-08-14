package shub39.momentum.home

import androidx.compose.runtime.Immutable
import shub39.momentum.core.domain.data_classes.ProjectListData

@Immutable
data class HomeState(
    val projects: List<ProjectListData> = emptyList(),
)