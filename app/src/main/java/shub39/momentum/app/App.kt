package shub39.momentum.app

import androidx.compose.runtime.Composable

private sealed interface Screens {
    data object Onboarding: Screens
    data object HomeGraph: Screens
    data object SettingsGraph: Screens
}
@Composable
fun App() {

}