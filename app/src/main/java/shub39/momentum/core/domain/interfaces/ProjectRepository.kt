package shub39.momentum.core.domain.interfaces

import kotlinx.coroutines.flow.Flow
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.ProjectListData

interface ProjectRepository {
    fun getProjectListData(): Flow<List<ProjectListData>>
    suspend fun upsertProject(project: Project)
    suspend fun deleteProject(project: Project)

    fun getDays(): Flow<List<Day>>
    suspend fun upsertDay(day: Day)
    suspend fun deleteDay(day: Day)
}