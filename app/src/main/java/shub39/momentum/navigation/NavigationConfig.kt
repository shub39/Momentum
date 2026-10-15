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
package shub39.momentum.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

sealed interface Routes : NavKey {
    data object Home : Routes

    data object Onboarding : Routes

    data object Paywall : Routes

    sealed interface Project : Routes {
        data object ProjectDetails : Routes

        data object ProjectCalendarView : Routes

        data object ProjectMontageView : Routes

        data class DayInfoView(val selectedDate: Long) : Routes

        data class Camera(val selectedDate: Long) : Routes
    }

    sealed interface Settings : Routes {
        data object SettingsRoot : Routes

        data object About : Routes

        data object LookAndFeel : Routes

        data object Changelog : Routes

        data object Backup : Routes
    }
}

class TopLevelBackstack(startKey: Routes = Routes.Home) {
    val backStack = mutableStateListOf<Routes>(startKey)

    private fun Routes.getSubtype() =
        when (this) {
            Onboarding,
            Home -> this::class
            is Project -> Routes.Project::class
            is Settings -> Routes.Settings::class
            else -> null
        }

    fun isRouteOnTop(route: Routes) = backStack.lastOrNull() == route

    fun add(route: Routes) {
        if (route is Routes.Settings) {
            val settingsRoutes = backStack.filterIsInstance<Routes.Settings>()
            backStack.removeAll(settingsRoutes)

            if (route != Routes.Settings.SettingsRoot) {
                backStack.add(Routes.Settings.SettingsRoot)
            }
            backStack.add(route)
        } else {
            // Remove if already exists to move it to the top
            backStack.remove(route)

            // Enforce max 2 unique routes per subtype
            val subtype = route.getSubtype()
            val existingOfSubtype = backStack.filter { it.getSubtype() == subtype }
            if (existingOfSubtype.size >= 2) {
                backStack.remove(existingOfSubtype.first())
            }

            backStack.add(route)
        }
    }

    fun removeLast() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}
