package shub39.momentum.project.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectMontage(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {},
//                navigationIcon = {
//                    IconButton(
//                        onClick = onNavigateBack
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
//                            contentDescription = "Navigate Back"
//                        )
//                    }
//                }
//            )
//        }
//    ) {
//
//    }
}

@Preview
@Composable
private fun Preview() {
    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
        )
    ) {
        ProjectMontage(
            state = ProjectState(),
            onAction = {},
            onNavigateBack = {}
        )
    }
}