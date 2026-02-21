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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import shub39.momentum.core.interfaces.SettingsPrefs
import shub39.momentum.presentation.onboarding.OnboardingAction
import shub39.momentum.presentation.onboarding.OnboardingState

@KoinViewModel
class OnboardingViewModel(private val datastore: SettingsPrefs) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state =
        _state
            .asStateFlow()
            .onStart {}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = OnboardingState(),
            )

    fun onAction(action: OnboardingAction) =
        viewModelScope.launch {
            when (action) {
                OnboardingAction.OnOnboardingDone -> datastore.updateOnboardingDone(true)
                is OnboardingAction.OnPermissionChange ->
                    _state.update { it.copy(isPermissionGranted = action.isGranted) }
            }
        }
}
