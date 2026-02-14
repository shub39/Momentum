package shub39.momentum.presentation.project

import android.content.Context
import shub39.momentum.domain.data_classes.AlarmData
import shub39.momentum.domain.data_classes.Day
import shub39.momentum.domain.data_classes.MontageConfig
import shub39.momentum.domain.data_classes.PlayerAction
import shub39.momentum.domain.data_classes.Project

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
}