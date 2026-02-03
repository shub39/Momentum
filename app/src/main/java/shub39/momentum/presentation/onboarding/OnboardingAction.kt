package shub39.momentum.presentation.onboarding

sealed interface OnboardingAction {
    data object OnOnboardingDone : OnboardingAction
    data class OnPermissionChange(val isGranted: Boolean) : OnboardingAction
}