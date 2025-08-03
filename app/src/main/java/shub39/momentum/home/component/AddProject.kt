package shub39.momentum.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.home.HomeAction
import shub39.momentum.home.HomeState

@Composable
fun AddProject(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

@Composable
@Preview
private fun Preview() {
    MomentumTheme(
        theme = Theme(
            seedColor = Color.Yellow,
            appTheme = AppTheme.DARK,
            font = Fonts.FIGTREE,
            paletteStyle = PaletteStyle.Fidelity
        )
    ) {
        AddProject(
            state = HomeState(),
            onAction = {}
        )
    }
}