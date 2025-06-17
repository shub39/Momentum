package shub39.momentum.onboarding

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Onboarding(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    onNavigateToHome: () -> Unit
) {
    Text(text = "Onboarding")
    Button(
        onClick = {
            onAction(OnboardingAction.OnOnboardingDone)
            onNavigateToHome()
        }
    ) {
        Text("Not done yet, skip")
    }
}