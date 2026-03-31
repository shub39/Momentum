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
import shub39.momentum.presentation.settings.ui.sections.Changelog
import shub39.momentum.presentation.settings.ui.sections.LookAndFeel
import shub39.momentum.presentation.settings.ui.sections.Root

@Serializable data object Root : NavKey

@Serializable data object LookAndFeel : NavKey

@Serializable data object Changelog : NavKey

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
    val backStack = rememberNavBackStack(Root)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider =
            entryProvider {
                entry<Root> {
                    Root(
                        onAction = onAction,
                        onNavigateBack = onNavigateBack,
                        onNavigateToOnboarding = onNavigateToOnboarding,
                        onNavigateToLookAndFeel = { backStack.add(LookAndFeel) },
                        onNavigateToPaywall = onNavigateToPaywall,
                        onNavigateToChangelog = { backStack.add(Changelog) },
                    )
                }

                entry<LookAndFeel>(metadata = horizontalTransitionMetadata()) {
                    LookAndFeel(
                        state = state,
                        onAction = onAction,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        isPlusUser = isPlusUser,
                        onNavigateToPaywall = onNavigateToPaywall,
                    )
                }

                entry<Changelog>(metadata = horizontalTransitionMetadata()) {
                    Changelog(
                        changelog = state.changelog,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }
            },
    )
}
