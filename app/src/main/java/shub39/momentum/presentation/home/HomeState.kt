package shub39.momentum.presentation.home

import androidx.compose.runtime.Immutable
import shub39.momentum.domain.data_classes.ProjectListData

@Immutable
data class HomeState(
    val projects: List<ProjectListData> = emptyList(),
)