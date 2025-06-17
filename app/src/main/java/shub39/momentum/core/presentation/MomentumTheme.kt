package shub39.momentum.core.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialTheme
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme

@Composable
fun MomentumTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
        seedColor = if (theme.isMaterialYou) {
            colorResource(android.R.color.system_accent1_200)
        } else {
            theme.seedColor
        },
        useDarkTheme = when (theme.appTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        },
        withAmoled = theme.isAmoled,
        style = theme.paletteStyle,
        typography = ProvideTypography(
            font = theme.font
        ),
        content = content
    )
}