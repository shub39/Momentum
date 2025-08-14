package shub39.momentum.onboarding

import androidx.compose.runtime.Immutable

@Immutable
data class OnboardingState(
    val isPermissionGranted: Boolean = false
)
