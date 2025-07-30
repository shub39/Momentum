package shub39.momentum.project

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import shub39.momentum.project.component.ProjectDetails

@Serializable
private sealed interface ProjectRoutes {
    @Serializable
    data object ProjectDetails : ProjectRoutes

    @Serializable
    data object ProjectCalendar : ProjectRoutes

    @Serializable
    data object ProjectShorts : ProjectRoutes
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
                onNavigateToVideoMaker = { navController.navigate(ProjectRoutes.ProjectCalendar) }
            )
        }

        composable<ProjectRoutes.ProjectCalendar> {

        }
    }
}