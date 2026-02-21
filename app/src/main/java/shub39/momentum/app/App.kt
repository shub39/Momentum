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
import org.koin.compose.viewmodel.koinViewModel
import shub39.momentum.billing.presentation.PaywallPage
import shub39.momentum.presentation.home.HomeGraph
import shub39.momentum.presentation.onboarding.Onboarding
import shub39.momentum.presentation.project.ProjectGraph
import shub39.momentum.presentation.settings.SettingsGraph
import shub39.momentum.presentation.shared.ChangelogDialog
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.viewmodels.HomeViewModel
import shub39.momentum.viewmodels.MainAppViewModel
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

    val mainViewModel: MainAppViewModel = koinInject()
    val state by mainViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isOnboardingDone) {
        if (!state.isOnboardingDone) {
            navController.navigate(Screens.Onboarding)
        }
    }

    MomentumTheme(theme = state.theme) {
        NavHost(
            navController = navController,
            startDestination = Screens.HomeGraph,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
        ) {
            composable<Screens.HomeGraph> {
                val homeViewModel: HomeViewModel = koinInject()
                val homeState by homeViewModel.state.collectAsStateWithLifecycle()

                HomeGraph(
                    state = homeState,
                    onAction = homeViewModel::onAction,
                    onNavigateToSettings = { navController.navigate(Screens.SettingsGraph) },
                    onNavigateToProject = { navController.navigate(Screens.ProjectGraph) },
                    isPlusUser = state.isPlusUser,
                    onNavigateToPaywall = { navController.navigate(Screens.PaywallPage) },
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
                    },
                )
            }

            composable<Screens.ProjectGraph> {
                val projectViewModel: ProjectViewModel = koinInject()
                val projectState by projectViewModel.state.collectAsStateWithLifecycle()
                val exoPlayer by projectViewModel.exoPlayer.collectAsStateWithLifecycle()

                ProjectGraph(
                    state = projectState,
                    exoPlayer = exoPlayer,
                    onAction = projectViewModel::onAction,
                    onNavigateBack = { navController.navigateUp() },
                    isPlusUser = state.isPlusUser,
                    onNavigateToPaywall = { navController.navigate(Screens.PaywallPage) },
                )
            }

            composable<Screens.SettingsGraph> {
                val settingsViewModel: SettingsViewModel = koinViewModel()
                val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

                SettingsGraph(
                    state = settingsState,
                    onAction = settingsViewModel::onAction,
                    onNavigateBack = { navController.navigateUp() },
                    isPlusUser = state.isPlusUser,
                    onNavigateToPaywall = { navController.navigate(Screens.PaywallPage) },
                )
            }

            composable<Screens.PaywallPage> {
                PaywallPage(
                    isPlusUser = state.isPlusUser,
                    onDismissRequest = {
                        mainViewModel.checkSubscription()
                        navController.navigateUp()
                    },
                )
            }
        }

        if (state.currentChangelog != null) {
            ChangelogDialog(
                currentLog = state.currentChangelog!!,
                onDismissRequest = { mainViewModel.dismissChangelog() },
            )
        }
    }
}
