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
package shub39.momentum.presentation.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.ProjectListData
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle
import shub39.momentum.presentation.home.ui.sections.ProjectList
import shub39.momentum.presentation.home.ui.sections.ProjectUpsertSheet
import shub39.momentum.presentation.shared.MomentumTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeGraph(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProject: () -> Unit,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showProjectUpsertSheet by remember { mutableStateOf(false) }

    ProjectList(
        modifier = modifier,
        state = state,
        onAction = onAction,
        onNavigateToProject = onNavigateToProject,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToNewProject = {
            if (state.projects.size <= 3 || isPlusUser) {
                showProjectUpsertSheet = true
            } else {
                onNavigateToPaywall()
            }
        },
    )

    if (showProjectUpsertSheet) {
        ProjectUpsertSheet(
            onAction = onAction,
            onDismissRequest = { showProjectUpsertSheet = false },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            HomeState(
                projects =
                    (0..10).map {
                        ProjectListData(
                            project =
                                Project(
                                    id = it.toLong(),
                                    title = "Project $it",
                                    description = "Description for project $it",
                                ),
                            last10Days = emptyList(),
                        )
                    }
            )
        )
    }

    MomentumTheme(
        theme =
            Theme(
                seedColor = Color.Yellow,
                appTheme = AppTheme.DARK,
                font = Fonts.FIGTREE,
                paletteStyle = PaletteStyle.FIDELITY,
            )
    ) {
        HomeGraph(
            state = state,
            onAction = {},
            onNavigateToSettings = {},
            onNavigateToProject = {},
            isPlusUser = true,
            onNavigateToPaywall = {},
        )
    }
}
