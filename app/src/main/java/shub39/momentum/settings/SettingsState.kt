package shub39.momentum.settings

import shub39.momentum.core.domain.data_classes.Theme

data class SettingsState(
    val theme: Theme = Theme(),
    val isOnboardingDone: Boolean = true
)
