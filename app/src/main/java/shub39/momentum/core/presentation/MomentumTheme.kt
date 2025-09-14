package shub39.momentum.core.presentation

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialExpressiveTheme
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MomentumTheme(
    theme: Theme = Theme(),
    content: @Composable () -> Unit
) {
    DynamicMaterialExpressiveTheme(
        seedColor = if (theme.isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            colorResource(android.R.color.system_accent1_200)
        } else {
            theme.seedColor
        },
        isDark = when (theme.appTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        },
        isAmoled = theme.isAmoled,
        style = theme.paletteStyle,
        typography = ProvideTypography(
            font = theme.font
        ),
        content = content
    )
}