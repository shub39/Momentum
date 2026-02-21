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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.interfaces.MontageState
import shub39.momentum.core.interfaces.ProjectRepository
import shub39.momentum.presentation.home.HomeAction
import shub39.momentum.presentation.home.HomeState

@KoinViewModel
class HomeViewModel(
    private val stateLayer: SharedState,
    private val projectRepository: ProjectRepository,
) : ViewModel() {
    private val _state = stateLayer.homeState
    val state =
        _state
            .asStateFlow()
            .onStart { observeProjects() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeState(),
            )

    fun onAction(action: HomeAction) =
        viewModelScope.launch {
            when (action) {
                is HomeAction.OnChangeProject ->
                    stateLayer.projectState.update {
                        it.copy(
                            project = action.project,
                            days = emptyList(),
                            montage = MontageState.ProcessingImages(),
                        )
                    }

                is HomeAction.OnAddProject -> {
                    projectRepository.upsertProject(
                        Project(title = action.title, description = action.description)
                    )
                }
            }
        }

    private fun observeProjects() =
        viewModelScope.launch {
            projectRepository
                .getProjectListData()
                .onEach { projects -> _state.update { it.copy(projects = projects) } }
                .launchIn(this)
        }
}
