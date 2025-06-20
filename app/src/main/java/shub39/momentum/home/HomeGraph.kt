package shub39.momentum.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.home.component.ProjectsList

@Serializable
private sealed interface HomeScreens {
    @Serializable
    data object ProjectsList : HomeScreens

    @Serializable
    data object Project : HomeScreens
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeGraph(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeScreens.ProjectsList
    ) {
        composable<HomeScreens.ProjectsList> {
            ProjectsList(
                state = state,
                onAction = onAction,
                onNavigateToProject = { navController.navigate(HomeScreens.Project) },
                onNavigateToSettings = onNavigateToSettings
            )
        }

        composable<HomeScreens.Project> {
            Text(text = "Project")
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            HomeState(
                isLoading = false
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            font = Fonts.INTER
        )
    ) {
        HomeGraph(
            state = state,
            onAction = {},
            onNavigateToSettings = {}
        )
    }
}