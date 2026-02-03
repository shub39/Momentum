package shub39.momentum.app

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import shub39.momentum.domain.data_classes.Theme

@Stable
@Immutable
data class MainAppState(
    val isOnboardingDone: Boolean = true,
    val theme: Theme = Theme(),
    val isPlusUser: Boolean = false
)
