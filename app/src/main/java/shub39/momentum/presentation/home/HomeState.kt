package shub39.momentum.presentation.home

import androidx.compose.runtime.Immutable
import shub39.momentum.core.data_classes.ProjectListData

@Immutable
data class HomeState(
    val projects: List<ProjectListData> = emptyList(),
)