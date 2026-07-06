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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import shub39.momentum.navigation.horizontalTransitionMetadata
import shub39.momentum.presentation.settings.ui.sections.About
import shub39.momentum.presentation.settings.ui.sections.Backup
import shub39.momentum.presentation.settings.ui.sections.Changelog
import shub39.momentum.presentation.settings.ui.sections.LookAndFeel
import shub39.momentum.presentation.settings.ui.sections.Root

@Serializable
private sealed interface Routes : NavKey {
    @Serializable data object Root : Routes

    @Serializable data object About : Routes

    @Serializable data object LookAndFeel : Routes

    @Serializable data object Changelog : Routes

    @Serializable data object Backup : Routes
}

@Composable
fun SettingsGraph(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Routes.Root)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider =
            entryProvider {
                entry<Routes.Root> {
                    Root(
                        onNavigateBack = onNavigateBack,
                        onNavigateToOnboarding = onNavigateToOnboarding,
                        onNavigateToLookAndFeel = { backStack.add(Routes.LookAndFeel) },
                        onNavigateToPaywall = onNavigateToPaywall,
                        onNavigateToChangelog = { backStack.add(Routes.Changelog) },
                        onNavigateToAppInfo = { backStack.add(Routes.About) },
                        onNavigateToBackup = { backStack.add(Routes.Backup) },
                        currentVersion = state.changelog.firstOrNull()?.version ?: "",
                    )
                }

                entry<Routes.LookAndFeel>(metadata = horizontalTransitionMetadata()) {
                    LookAndFeel(
                        state = state,
                        onAction = onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        isPlusUser = isPlusUser,
                        onNavigateToPaywall = onNavigateToPaywall,
                    )
                }

                entry<Routes.Changelog>(metadata = horizontalTransitionMetadata()) {
                    Changelog(
                        changelog = state.changelog,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }

                entry<Routes.About>(metadata = horizontalTransitionMetadata()) {
                    About(
                        versionName = state.changelog.firstOrNull()?.version ?: "",
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }

                entry<Routes.Backup>(metadata = horizontalTransitionMetadata()) {
                    Backup(
                        state = state,
                        onAction = onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }
            },
    )
}
