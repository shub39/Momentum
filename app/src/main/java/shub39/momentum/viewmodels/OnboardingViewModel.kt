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
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import shub39.momentum.core.interfaces.SettingsPrefs
import shub39.momentum.presentation.onboarding.OnboardingAction

@KoinViewModel
class OnboardingViewModel(private val datastore: SettingsPrefs) : ViewModel() {
    fun onAction(action: OnboardingAction) =
        viewModelScope.launch {
            when (action) {
                OnOnboardingDone -> datastore.updateOnboardingDone(true)
            }
        }
}
