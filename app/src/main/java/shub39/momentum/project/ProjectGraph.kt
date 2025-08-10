package shub39.momentum.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ui.component.DayInfoSheet
import shub39.momentum.project.ui.sections.ProjectCalendar
import shub39.momentum.project.ui.sections.ProjectDetails
import shub39.momentum.project.ui.sections.ProjectMontageView

@Serializable
private sealed interface ProjectRoutes {
    @Serializable
    data object ProjectDetails : ProjectRoutes
    @Serializable
    data object ProjectCalendarView : ProjectRoutes
    @Serializable
    data object ProjectShortsView : ProjectRoutes
    @Serializable
    data object ProjectMontageView : ProjectRoutes
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectGraph(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    AnimatedContent(
        targetState = state.project,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { project ->
        if (project == null) {
            LoadingIndicator()
        } else {
            LaunchedEffect(project) {
                onAction(ProjectAction.OnUpdateDays)
                onAction(ProjectAction.OnInitializeExoplayer(context))
            }

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = ProjectRoutes.ProjectDetails,
                enterTransition = { fadeIn(tween(500)) },
                exitTransition = { fadeOut(tween(500)) },
                popEnterTransition = { fadeIn(tween(500)) },
                popExitTransition = { fadeOut(tween(500)) },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {
                composable<ProjectRoutes.ProjectDetails> {
                    ProjectDetails(
                        state = state,
                        onAction = onAction,
                        onNavigateBack = onNavigateBack,
                        onNavigateToCalendar = { navController.navigate(ProjectRoutes.ProjectCalendarView) },
                        onNavigateToMontage = { navController.navigate(ProjectRoutes.ProjectMontageView) }
                    )
                }

                composable<ProjectRoutes.ProjectCalendarView> {
                    ProjectCalendar(
                        state = state,
                        onAction = onAction,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }

                composable<ProjectRoutes.ProjectShortsView> {

                }

                composable<ProjectRoutes.ProjectMontageView> {
                    ProjectMontageView(
                        state = state,
                        onAction = onAction,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }

            state.selectedDate?.let {
                DayInfoSheet(
                    selectedDate = it,
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            ProjectState(
                project = Project(
                    id = 1,
                    title = "Sample Project",
                    description = "A sample project",
                )
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        ProjectGraph(
            state = state,
            onAction = {},
            onNavigateBack = {}
        )
    }
}