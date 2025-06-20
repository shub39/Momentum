package shub39.momentum.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.presentation.MomentumTheme

@Serializable
private sealed interface HomeScreens {
    @Serializable
    data object Home : HomeScreens
    @Serializable
    data object Project : HomeScreens
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeGraph(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                subtitle = { Text(text = stringResource(R.string.projects)) },
                actions = {
                    FilledTonalIconButton(
                        onClick = onNavigateToSettings,
                        shapes = IconButtonShapes(
                            shape = CircleShape,
                            pressedShape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = HomeScreens.Home
        ) {
            composable<HomeScreens.Home> {
                Text(text = "Home")
            }

            composable<HomeScreens.Project> {
                Text(text = "Project")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember { mutableStateOf(HomeState()) }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            font = Fonts.INTER
        )
    ) {
        HomeGraph(
            state = state,
            onAction = {}
        )
    }
}