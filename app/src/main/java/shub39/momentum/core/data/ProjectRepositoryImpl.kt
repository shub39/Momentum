package shub39.momentum.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shub39.momentum.core.data.database.DaysDao
import shub39.momentum.core.data.database.ProjectDao
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.interfaces.ProjectRepository

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao,
    private val daysDao: DaysDao
) : ProjectRepository {
    override fun getProjects(): Flow<List<Project>> {
        return projectDao
            .getProjects()
            .map { flow -> flow.map { it.toProject() } }
    }

    override suspend fun upsertProject(project: Project) {
        projectDao.upsertProject(project.toEntity())
    }

    override suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project.toEntity())
    }

    override fun getDays(): Flow<List<Day>> {
        return daysDao
            .getDays()
            .map { flow -> flow.map { it.toDays() } }
    }

    override suspend fun upsertDay(day: Day) {
        return daysDao.upsertDay(day.toDayEntity())
    }

    override suspend fun deleteDay(day: Day) {
        return daysDao.deleteDay(day.toDayEntity())
    }
}