package shub39.momentum.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.materialkolor.PaletteStyle
import kotlinx.serialization.Serializable
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.ProjectListData
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.home.ui.sections.AddProject
import shub39.momentum.home.ui.sections.ProjectList
import java.time.LocalDate

@Serializable
private sealed interface HomeRoutes {
    @Serializable
    data object ProjectList : HomeRoutes

    @Serializable
    data object AddProject : HomeRoutes
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeGraph(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProject: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoutes.ProjectList,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        composable<HomeRoutes.ProjectList> {
            ProjectList(
                state = state,
                onAction = onAction,
                onNavigateToProject = onNavigateToProject,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToNewProject = { navController.navigate(HomeRoutes.AddProject) }
            )
        }

        composable<HomeRoutes.AddProject> {
            AddProject(
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            HomeState(
                projects = (0..10).map {
                    ProjectListData(
                        project = Project(
                            id = it.toLong(),
                            title = "Project $it",
                            description = "Description for project $it",
                        ),
                        lastDay = Day(
                            id = it.toLong(),
                            projectId = it.toLong(),
                            image = "",
                            comment = "",
                            date = LocalDate.now().toEpochDay(),
                            isFavorite = false
                        )
                    )
                }
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            seedColor = Color.Yellow,
            appTheme = AppTheme.DARK,
            font = Fonts.FIGTREE,
            paletteStyle = PaletteStyle.Fidelity
        )
    ) {
        HomeGraph(
            state = state,
            onAction = {},
            onNavigateToSettings = {},
            onNavigateToProject = {}
        )
    }
}