package shub39.momentum.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.home.HomeGraph
import shub39.momentum.onboarding.Onboarding
import shub39.momentum.project.ProjectGraph
import shub39.momentum.settings.SettingsGraph
import shub39.momentum.viewmodels.HomeViewModel
import shub39.momentum.viewmodels.OnboardingViewModel
import shub39.momentum.viewmodels.ProjectViewModel
import shub39.momentum.viewmodels.SettingsViewModel

@Serializable
private sealed interface Screens {
    @Serializable
    data object Onboarding : Screens
    @Serializable
    data object HomeGraph : Screens
    @Serializable
    data object ProjectGraph : Screens
    @Serializable
    data object SettingsGraph : Screens
    @Serializable
    data object PaywallPage : Screens
}

@Composable
fun App() {
    val navController = rememberNavController()

    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

    // go to onboarding if first launch
    LaunchedEffect(settingsState.isOnboardingDone) {
        if (!settingsState.isOnboardingDone) {
            navController.navigate(Screens.Onboarding) {
                popUpTo(Screens.HomeGraph) { inclusive = true }
            }
        }
    }

    MomentumTheme(
        theme = settingsState.theme
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.HomeGraph,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            composable<Screens.HomeGraph> {
                val homeViewModel: HomeViewModel = koinInject()
                val homeState by homeViewModel.state.collectAsStateWithLifecycle()

                HomeGraph(
                    state = homeState,
                    onAction = homeViewModel::onAction,
                    onNavigateToSettings = { navController.navigate(Screens.SettingsGraph) },
                    onNavigateToProject = { navController.navigate(Screens.ProjectGraph) }
                )
            }

            composable<Screens.Onboarding> {
                val onboardingViewModel: OnboardingViewModel = koinInject()
                val onboardingState by onboardingViewModel.state.collectAsStateWithLifecycle()

                Onboarding(
                    state = onboardingState,
                    onAction = onboardingViewModel::onAction,
                    onNavigateToHome = {
                        navController.navigate(Screens.HomeGraph) {
                            popUpTo(Screens.Onboarding) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screens.ProjectGraph> {
                val projectViewModel: ProjectViewModel = koinInject()
                val projectState by projectViewModel.state.collectAsStateWithLifecycle()

                ProjectGraph(
                    state = projectState,
                    onAction = projectViewModel::onAction,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable<Screens.SettingsGraph> {
                SettingsGraph(
                    state = settingsState,
                    onAction = settingsViewModel::onAction,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}