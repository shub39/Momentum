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
import shub39.momentum.viewmodels.SettingsViewModel

private sealed interface Screens {
    @Serializable data object Onboarding: Screens
    @Serializable data object HomeGraph: Screens
    @Serializable data object SettingsGraph: Screens
}
@Composable
fun App(
    settingsViewModel: SettingsViewModel = koinInject()
) {
    val navController = rememberNavController()

    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (!settingsState.isOnboardingDone) {
            navController.navigate(Screens.Onboarding) {
                popUpTo(Screens.Onboarding) { inclusive = true }
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
                .fillMaxSize(),
        ) {
            composable<Screens.HomeGraph> {

            }

            composable<Screens.Onboarding> {

            }

            composable<Screens.SettingsGraph> {

            }
        }
    }
}