package shub39.momentum.presentation.settings

import androidx.compose.runtime.Immutable
import shub39.momentum.domain.data_classes.Theme

@Immutable
data class SettingsState(
    val theme: Theme = Theme(),
    val isOnboardingDone: Boolean = true,
)
