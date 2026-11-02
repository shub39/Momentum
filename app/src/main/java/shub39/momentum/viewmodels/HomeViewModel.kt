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
package shub39.momentum.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import shub39.momentum.core.data_classes.AnalyticsEvent
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.interfaces.AnalyticsWrapper
import shub39.momentum.core.interfaces.ProjectRepository
import shub39.momentum.core.interfaces.ProjectSelection
import shub39.momentum.presentation.home.HomeAction
import shub39.momentum.presentation.home.HomeState

@KoinViewModel
class HomeViewModel(
    private val selection: ProjectSelection,
    private val projectRepository: ProjectRepository,
    private val analytics: AnalyticsWrapper,
) : ViewModel() {
    val state =
        projectRepository
            .getProjectListData()
            .map { projects -> HomeState(projects = projects) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeState(),
            )

    fun onAction(action: HomeAction) =
        viewModelScope.launch {
            when (action) {
                is OnChangeProject -> {
                    analytics.trackEvent(AnalyticsEvent.PROJECT_SELECTED)
                    selection.selectProject(action.projectId)
                }

                is OnAddProject -> {
                    analytics.trackEvent(
                        AnalyticsEvent.PROJECT_CREATED,
                        mapOf("project_count" to (state.value.projects.size + 1)),
                    )
                    projectRepository.upsertProject(
                        Project(title = action.title, description = action.description)
                    )
                }
            }
        }
}
