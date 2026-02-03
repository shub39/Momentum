package shub39.momentum.presentation.onboarding

import androidx.compose.runtime.Immutable

@Immutable
data class OnboardingState(
    val isPermissionGranted: Boolean = false
)
