package shub39.momentum.core.interfaces

import kotlinx.coroutines.flow.Flow
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.ProjectListData

interface ProjectRepository {
    fun getProjectListData(): Flow<List<ProjectListData>>
    suspend fun upsertProject(project: Project)
    suspend fun deleteProject(project: Project)

    suspend fun getProjectById(id: Long): Project?

    fun getDays(): Flow<List<Day>>
    suspend fun upsertDay(day: Day)
    suspend fun deleteDay(day: Day)

    suspend fun getLastCompletedDay(projectId: Long): Day?
}