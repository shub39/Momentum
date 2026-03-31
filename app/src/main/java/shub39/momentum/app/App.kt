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

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import shub39.momentum.billing.presentation.PaywallPage
import shub39.momentum.presentation.home.HomeGraph
import shub39.momentum.presentation.onboarding.Onboarding
import shub39.momentum.presentation.project.ProjectGraph
import shub39.momentum.presentation.settings.SettingsGraph
import shub39.momentum.presentation.shared.ChangelogSheet
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.viewmodels.HomeViewModel
import shub39.momentum.viewmodels.MainAppViewModel
import shub39.momentum.viewmodels.OnboardingViewModel
import shub39.momentum.viewmodels.ProjectViewModel
import shub39.momentum.viewmodels.SettingsViewModel

@Serializable
data object Onboarding : NavKey

@Serializable
data object HomeGraph : NavKey

@Serializable
data object ProjectGraph : NavKey

@Serializable
data object SettingsGraph : NavKey

@Serializable
data object PaywallPage : NavKey

@Composable
fun App() {
    val backStack = rememberNavBackStack(HomeGraph)

    val mainViewModel: MainAppViewModel = koinInject()
    val state by mainViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isOnboardingDone) {
        if (!state.isOnboardingDone) {
            backStack.add(Onboarding)
        }
    }

    MomentumTheme(theme = state.theme) {
        NavDisplay(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<HomeGraph> {
                    val homeViewModel: HomeViewModel = koinInject()
                    val homeState by homeViewModel.state.collectAsStateWithLifecycle()

                    HomeGraph(
                        state = homeState,
                        onAction = homeViewModel::onAction,
                        onNavigateToSettings = { backStack.add(SettingsGraph) },
                        onNavigateToProject = { backStack.add(ProjectGraph) },
                        isPlusUser = state.isPlusUser,
                        onNavigateToPaywall = { backStack.add(PaywallPage) },
                    )
                }

                entry<Onboarding>(
                    metadata = metadata {
                        put(NavDisplay.TransitionKey) {
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300)
                            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                        }
                        put(NavDisplay.PopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                        put(NavDisplay.PredictivePopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                    }
                ) {
                    val onboardingViewModel: OnboardingViewModel = koinViewModel()
                    val onboardingState by onboardingViewModel.state.collectAsStateWithLifecycle()

                    Onboarding(
                        state = onboardingState,
                        onAction = onboardingViewModel::onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }

                entry<ProjectGraph>(
                    metadata = metadata {
                        put(NavDisplay.TransitionKey) {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300)
                            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                        }
                        put(NavDisplay.PopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                        put(NavDisplay.PredictivePopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                    }
                ) {
                    val projectViewModel: ProjectViewModel = koinInject()
                    val projectState by projectViewModel.state.collectAsStateWithLifecycle()
                    val exoPlayer by projectViewModel.exoPlayer.collectAsStateWithLifecycle()

                    ProjectGraph(
                        state = projectState,
                        exoPlayer = exoPlayer,
                        onAction = projectViewModel::onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        isPlusUser = state.isPlusUser,
                        onNavigateToPaywall = { backStack.add(PaywallPage) },
                    )
                }

                entry<SettingsGraph>(
                    metadata = metadata {
                        put(NavDisplay.TransitionKey) {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300)
                            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                        }
                        put(NavDisplay.PopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                        put(NavDisplay.PredictivePopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                    }
                ) {
                    val settingsViewModel: SettingsViewModel = koinViewModel()
                    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

                    SettingsGraph(
                        state = settingsState,
                        onAction = settingsViewModel::onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        isPlusUser = state.isPlusUser,
                        onNavigateToPaywall = { backStack.add(PaywallPage) },
                    )
                }

                entry<PaywallPage>(
                    metadata = metadata {
                        put(NavDisplay.TransitionKey) {
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300)
                            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                        }
                        put(NavDisplay.PopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                        put(NavDisplay.PredictivePopTransitionKey) {
                            EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(300)
                                    )
                        }
                    }
                ) {
                    PaywallPage(
                        isPlusUser = state.isPlusUser,
                        onDismissRequest = {
                            mainViewModel.checkSubscription()
                            if (backStack.size != 1) backStack.removeLastOrNull()
                        },
                    )
                }
            }
        )

        if (state.currentChangelog != null) {
            ChangelogSheet(
                currentLog = state.currentChangelog!!,
                onDismissRequest = { mainViewModel.dismissChangelog() },
            )
        }
    }
}
