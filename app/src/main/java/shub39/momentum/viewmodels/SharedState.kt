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

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single
import shub39.momentum.presentation.home.HomeState
import shub39.momentum.presentation.project.ProjectState

@Single
class SharedState {
    val projectState: MutableStateFlow<ProjectState> = MutableStateFlow(ProjectState())
    val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
}
