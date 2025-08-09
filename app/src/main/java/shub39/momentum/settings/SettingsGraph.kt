package shub39.momentum.settings

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import shub39.momentum.settings.ui.sections.LookAndFeel
import shub39.momentum.settings.ui.sections.Root

@Serializable
private sealed interface SettingsRoutes {
    @Serializable
    data object Root : SettingsRoutes

    @Serializable
    data object LookAndFeel : SettingsRoutes
}

@Composable
fun SettingsGraph(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.Root
    ) {
        composable<SettingsRoutes.Root> {
            Root(
                onNavigateBack = onNavigateBack,
                onNavigateToLookAndFeel = { navController.navigate(SettingsRoutes.LookAndFeel) }
            )
        }

        composable<SettingsRoutes.LookAndFeel> {
            LookAndFeel(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}