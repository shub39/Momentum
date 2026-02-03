package shub39.momentum.settings

import androidx.compose.runtime.Immutable
import shub39.momentum.domain.data_classes.Theme

@Immutable
data class SettingsState(
    val theme: Theme = Theme(),
    val isOnboardingDone: Boolean = true,
    val isPlusUser: Boolean = false,
    val showPaywall: Boolean = false
)
