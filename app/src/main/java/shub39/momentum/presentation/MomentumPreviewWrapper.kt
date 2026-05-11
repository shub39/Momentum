package shub39.momentum.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.presentation.shared.MomentumTheme

class MomentumPreviewWrapper: PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        MomentumTheme(
            theme = Theme(appTheme = AppTheme.SYSTEM),
            content = content
        )
    }
}