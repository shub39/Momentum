package shub39.momentum.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import shub39.momentum.core.domain.interfaces.SettingsPrefs
import shub39.momentum.settings.SettingsAction
import shub39.momentum.settings.SettingsState

class SettingsViewModel(
    private val stateLayer: StateLayer,
    private val datastore: SettingsPrefs
): ViewModel() {
    private var observeJob: Job? = null
    private val _state = stateLayer.settingsState
    val state = _state.asStateFlow()
        .onStart { observeDatastore() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

    fun onAction(action: SettingsAction) = viewModelScope.launch {
        when (action) {
            is SettingsAction.OnAmoledSwitch -> datastore.updateAmoledPref(action.amoled)
            is SettingsAction.OnFontChange -> datastore.updateFonts(action.fonts)
            is SettingsAction.OnMaterialThemeToggle -> datastore.updateMaterialTheme(action.pref)
            is SettingsAction.OnPaletteChange -> datastore.updatePaletteStyle(action.style)
            is SettingsAction.OnSeedColorChange -> datastore.updateSeedColor(action.color)
            is SettingsAction.OnThemeSwitch -> datastore.updateAppThemePref(action.appTheme)
            is SettingsAction.OnOnboardingToggle -> datastore.updateOnboardingDone(action.done)
        }
    }

    private fun observeDatastore() = viewModelScope.launch {
    }
}