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
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.core.interfaces.SettingsPrefs
import shub39.momentum.data.ChangelogManager
import shub39.momentum.presentation.settings.SettingsAction
import shub39.momentum.presentation.settings.SettingsState

@KoinViewModel
class SettingsViewModel(
    private val datastore: SettingsPrefs,
    private val changelogManager: ChangelogManager,
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
                is SettingsAction.OnAmoledSwitch -> datastore.updateAmoledPref(action.amoled)
                is SettingsAction.OnFontChange -> datastore.updateFonts(action.fonts)
                is SettingsAction.OnMaterialThemeToggle ->
                    datastore.updateMaterialTheme(action.pref)

                is SettingsAction.OnPaletteChange -> datastore.updatePaletteStyle(action.style)
                is SettingsAction.OnSeedColorChange -> datastore.updateSeedColor(action.color)
                is SettingsAction.OnThemeSwitch -> datastore.updateAppThemePref(action.appTheme)
                is SettingsAction.OnOnboardingToggle -> datastore.updateOnboardingDone(action.done)
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

            datastore
                .getOnboardingDoneFlow()
                .onEach { pref -> _state.update { it.copy(isOnboardingDone = pref) } }
                .launchIn(this)
        }
}
