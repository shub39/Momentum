package shub39.momentum.project

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.component.ProjectCalendar
import shub39.momentum.project.component.ProjectDetails
import shub39.momentum.project.component.ProjectMontageView

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

@Composable
fun ProjectGraph(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProjectRoutes.ProjectDetails,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) },
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
                onNavigateBack = { navController.navigateUp() },
                onNavigateToMontage = { navController.navigate(ProjectRoutes.ProjectMontageView) }
            )
        }

        composable<ProjectRoutes.ProjectShortsView> {

        }

        composable<ProjectRoutes.ProjectMontageView> {
            ProjectMontageView(
                state = state,
                onAction = onAction
            )
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
                    startDate = 1,
                    lastUpdatedDate = 1
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