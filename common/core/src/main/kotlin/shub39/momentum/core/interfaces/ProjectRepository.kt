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
