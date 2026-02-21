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
package shub39.momentum.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import shub39.momentum.presentation.settings.ui.sections.Changelog
import shub39.momentum.presentation.settings.ui.sections.LookAndFeel
import shub39.momentum.presentation.settings.ui.sections.Root

@Serializable
private sealed interface SettingsRoutes {
    @Serializable
    data object Root : SettingsRoutes

    @Serializable
    data object LookAndFeel : SettingsRoutes

    @Serializable
    data object Changelog : SettingsRoutes
}

@Composable
fun SettingsGraph(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.Root,
        modifier = modifier,
    ) {
        composable<SettingsRoutes.Root> {
            Root(
                onAction = onAction,
                onNavigateBack = onNavigateBack,
                onNavigateToLookAndFeel = { navController.navigate(SettingsRoutes.LookAndFeel) },
                onNavigateToPaywall = onNavigateToPaywall,
                onNavigateToChangelog = { navController.navigate(SettingsRoutes.Changelog) },
            )
        }

        composable<SettingsRoutes.LookAndFeel> {
            LookAndFeel(
                state = state,
                onAction = onAction,
                onNavigateBack = { navController.navigateUp() },
                isPlusUser = isPlusUser,
                onNavigateToPaywall = onNavigateToPaywall,
            )
        }

        composable<SettingsRoutes.Changelog> {
            Changelog(changelog = state.changelog, onNavigateBack = { navController.navigateUp() })
        }
    }
}
