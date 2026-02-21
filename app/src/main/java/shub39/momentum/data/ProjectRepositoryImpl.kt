/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.momentum.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.ProjectListData
import shub39.momentum.core.interfaces.ProjectRepository
import shub39.momentum.data.database.DaysDao
import shub39.momentum.data.database.ProjectDao

@Single(binds = [ProjectRepository::class])
class ProjectRepositoryImpl(private val projectDao: ProjectDao, private val daysDao: DaysDao) :
    ProjectRepository {
    override fun getProjectListData(): Flow<List<ProjectListData>> {
        return projectDao.getProjects().map { flow ->
            flow.map { project ->
                ProjectListData(
                    project = project.toProject(),
                    last10Days = daysDao.getLastDaysById(project.id).map { it.toDay() },
                )
            }
        }
    }

    override suspend fun upsertProject(project: Project) {
        projectDao.upsertProject(project.toEntity())
    }

    override suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project.toEntity())
    }

    override suspend fun getProjectById(id: Long): Project? {
        return projectDao.getProjectById(id)?.toProject()
    }

    override fun getDays(): Flow<List<Day>> {
        return daysDao.getDays().map { flow -> flow.map { it.toDay() } }
    }

    override suspend fun upsertDay(day: Day) {
        return daysDao.upsertDay(day.toDayEntity())
    }

    override suspend fun deleteDay(day: Day) {
        return daysDao.deleteDay(day.toDayEntity())
    }

    override suspend fun getLastCompletedDay(projectId: Long): Day? {
        return daysDao.getLastDaysById(projectId).firstOrNull()?.toDay()
    }
}
