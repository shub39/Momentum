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
package shub39.momentum.presentation.project

import android.content.Context
import shub39.momentum.core.data_classes.AlarmData
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageConfig
import shub39.momentum.core.data_classes.PlayerAction
import shub39.momentum.core.data_classes.Project

sealed interface ProjectAction {
    data class OnInitializeExoPlayer(val context: Context) : ProjectAction

    data class OnPlayerAction(val playerAction: PlayerAction) : ProjectAction

    data class OnUpdateReminder(val alarmData: AlarmData? = null) : ProjectAction

    data object OnUpdateDays : ProjectAction

    data class OnUpdateProject(val project: Project) : ProjectAction

    data class OnDeleteProject(val project: Project) : ProjectAction

    data class OnUpsertDay(val day: Day, val isNewImage: Boolean = false) : ProjectAction

    data class OnDeleteDay(val day: Day) : ProjectAction

    data class OnCreateMontage(val days: List<Day>) : ProjectAction

    data object OnClearMontageState : ProjectAction

    data class OnEditMontageConfig(val config: MontageConfig) : ProjectAction

    data object OnResetMontagePrefs : ProjectAction

    data object OnStartFaceScan : ProjectAction

    data object OnResetScanState : ProjectAction
}
