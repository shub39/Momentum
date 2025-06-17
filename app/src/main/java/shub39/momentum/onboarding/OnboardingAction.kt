package shub39.momentum.onboarding

sealed interface OnboardingAction {
    data object OnOnboardingDone : OnboardingAction
    data class OnPermissionChange(val isGranted: Boolean) : OnboardingAction
}