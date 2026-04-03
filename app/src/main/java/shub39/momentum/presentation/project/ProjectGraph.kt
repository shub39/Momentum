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
package shub39.momentum.presentation.project

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.navigation.horizontalTransitionMetadata
import shub39.momentum.navigation.verticalTransitionMetadata
import shub39.momentum.presentation.project.ui.sections.DayInfo
import shub39.momentum.presentation.project.ui.sections.ProjectCalendar
import shub39.momentum.presentation.project.ui.sections.ProjectDetails
import shub39.momentum.presentation.project.ui.sections.ProjectMontageView
import shub39.momentum.presentation.shared.MomentumTheme

@Serializable data object ProjectDetails : NavKey

@Serializable data object ProjectCalendarView : NavKey

@Serializable data object ProjectMontageView : NavKey

@Serializable data class DayInfoView(val selectedDate: Long) : NavKey

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectGraph(
    state: ProjectState,
    exoPlayer: ExoPlayer?,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(ProjectDetails)

    LaunchedEffect(state.project) { onAction(ProjectAction.OnUpdateDays) }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider =
            entryProvider {
                entry<ProjectDetails> {
                    ProjectDetails(
                        state = state,
                        onAction = onAction,
                        onNavigateBack = onNavigateBack,
                        onNavigateToCalendar = { backStack.add(ProjectCalendarView) },
                        onNavigateToMontage = { backStack.add(ProjectMontageView) },
                        onNavigateToDayInfo = { backStack.add(DayInfoView(it)) },
                    )
                }

                entry<ProjectCalendarView>(metadata = verticalTransitionMetadata()) {
                    ProjectCalendar(
                        state = state,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        onNavigateToDayInfo = { backStack.add(DayInfoView(it)) },
                    )
                }

                entry<DayInfoView>(metadata = verticalTransitionMetadata()) {
                    DayInfo(
                        selectedDate = it.selectedDate,
                        state = state,
                        onAction = onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }

                entry<ProjectMontageView>(metadata = horizontalTransitionMetadata()) {
                    ProjectMontageView(
                        state = state,
                        exoPlayer = exoPlayer,
                        onAction = onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        isPlusUser = isPlusUser,
                        onNavigateToPaywall = onNavigateToPaywall,
                    )
                }
            },
    )
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            ProjectState(
                project =
                    Project(id = 1, title = "Sample Project", description = "A sample project")
            )
        )
    }

    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        ProjectGraph(
            state = state,
            onAction = {},
            onNavigateBack = {},
            exoPlayer = null,
            isPlusUser = true,
            onNavigateToPaywall = {},
        )
    }
}
