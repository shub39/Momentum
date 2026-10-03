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
package shub39.momentum.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import shub39.momentum.core.backup.ExportRepo
import shub39.momentum.core.backup.ExportResult
import shub39.momentum.core.backup.ExportState
import shub39.momentum.core.backup.ImportRepo
import shub39.momentum.core.backup.ImportResult
import shub39.momentum.core.backup.ImportState
import shub39.momentum.core.data_classes.AnalyticsEvent
import shub39.momentum.core.interfaces.AnalyticsWrapper
import shub39.momentum.core.interfaces.SettingsPrefs
import shub39.momentum.data.ChangelogManager
import shub39.momentum.presentation.settings.SettingsAction
import shub39.momentum.presentation.settings.SettingsState

@KoinViewModel
class SettingsViewModel(
    private val importRepo: ImportRepo,
    private val exportRepo: ExportRepo,
    private val datastore: SettingsPrefs,
    private val changelogManager: ChangelogManager,
    private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state =
        _state
            .asStateFlow()
            .onStart {
                observeDatastore()
                getChangeLogs()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SettingsState(),
            )

    fun onAction(action: SettingsAction) =
        viewModelScope.launch {
            when (action) {
                is OnAmoledSwitch -> {
                    analytics.trackEvent(
                        AnalyticsEvent.THEME_CHANGED,
                        mapOf("setting" to "amoled", "value" to action.amoled),
                    )
                    datastore.updateAmoledPref(action.amoled)
                }

                is OnFontChange -> {
                    analytics.trackEvent(
                        AnalyticsEvent.THEME_CHANGED,
                        mapOf("setting" to "font", "value" to action.fonts.name),
                    )
                    datastore.updateFonts(action.fonts)
                }

                is OnMaterialThemeToggle -> {
                    analytics.trackEvent(
                        AnalyticsEvent.THEME_CHANGED,
                        mapOf("setting" to "material_you", "value" to action.pref),
                    )
                    datastore.updateMaterialTheme(action.pref)
                }

                is OnPaletteChange -> {
                    analytics.trackEvent(
                        AnalyticsEvent.THEME_CHANGED,
                        mapOf("setting" to "palette", "value" to action.style.name),
                    )
                    datastore.updatePaletteStyle(action.style)
                }

                is OnSeedColorChange -> {
                    analytics.trackEvent(
                        AnalyticsEvent.THEME_CHANGED,
                        mapOf("setting" to "seed_color"),
                    )
                    datastore.updateSeedColor(action.color)
                }

                is OnThemeSwitch -> {
                    analytics.trackEvent(
                        AnalyticsEvent.THEME_CHANGED,
                        mapOf("setting" to "app_theme", "value" to action.appTheme.name),
                    )
                    datastore.updateAppThemePref(action.appTheme)
                }

                SettingsAction.OnExportData -> {
                    _state.update { it.copy(exportState = ExportState.EXPORTING) }

                    val result = exportRepo.exportProjects()
                    analytics.trackEvent(
                        AnalyticsEvent.BACKUP_CREATED,
                        mapOf("success" to (result is ExportResult.Success)),
                    )

                    _state.update {
                        it.copy(
                            exportState =
                                when (result) {
                                    is ExportResult.Failure -> ExportState.FAILURE
                                    ExportResult.Success -> ExportState.EXPORTED
                                }
                        )
                    }

                    viewModelScope.launch {
                        delay(3000.milliseconds)
                        _state.update { it.copy(exportState = ExportState.IDLE) }
                    }
                }

                SettingsAction.OnImportData -> {
                    _state.update { it.copy(importState = ImportState.IMPORTING) }

                    val result = importRepo.restoreData()
                    analytics.trackEvent(
                        AnalyticsEvent.BACKUP_RESTORED,
                        mapOf("success" to (result is ImportResult.Success)),
                    )

                    _state.update {
                        it.copy(
                            importState =
                                when (result) {
                                    is ImportResult.Failure -> ImportState.FAILURE
                                    ImportResult.Success -> ImportState.IMPORTED
                                }
                        )
                    }

                    viewModelScope.launch {
                        delay(3000.milliseconds)
                        _state.update { it.copy(importState = ImportState.IDLE) }
                    }
                }

                SettingsAction.OnOpenSettings ->
                    analytics.trackEvent(AnalyticsEvent.SETTINGS_OPENED)

                SettingsAction.OnOpenAbout -> analytics.trackEvent(AnalyticsEvent.ABOUT_OPENED)

                SettingsAction.OnOpenChangelog ->
                    analytics.trackEvent(AnalyticsEvent.CHANGELOG_OPENED)
            }
        }

    private fun getChangeLogs() {
        viewModelScope.launch {
            _state.update { it.copy(changelog = changelogManager.changelogs.first()) }
        }
    }

    private fun observeDatastore() =
        viewModelScope.launch {
            datastore
                .getAmoledPrefFlow()
                .onEach { pref ->
                    _state.update { it.copy(theme = it.theme.copy(isAmoled = pref)) }
                }
                .launchIn(this)

            datastore
                .getFontFlow()
                .onEach { pref -> _state.update { it.copy(theme = it.theme.copy(font = pref)) } }
                .launchIn(this)

            datastore
                .getMaterialYouFlow()
                .onEach { pref ->
                    _state.update { it.copy(theme = it.theme.copy(isMaterialYou = pref)) }
                }
                .launchIn(this)

            datastore
                .getPaletteStyle()
                .onEach { pref ->
                    _state.update { it.copy(theme = it.theme.copy(paletteStyle = pref)) }
                }
                .launchIn(this)

            datastore
                .getSeedColorFlow()
                .onEach { pref ->
                    _state.update { it.copy(theme = it.theme.copy(seedColor = pref)) }
                }
                .launchIn(this)

            datastore
                .getAppThemePrefFlow()
                .onEach { pref ->
                    _state.update { it.copy(theme = it.theme.copy(appTheme = pref)) }
                }
                .launchIn(this)
        }
}
