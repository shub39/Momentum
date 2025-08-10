package shub39.momentum.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Onboarding(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    onNavigateToHome: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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
    }
}