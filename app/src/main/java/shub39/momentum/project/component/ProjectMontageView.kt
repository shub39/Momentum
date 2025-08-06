package shub39.momentum.project.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import shub39.momentum.core.domain.interfaces.MontageState
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectMontageView(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(ProjectAction.OnCreateMontage(state.days))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state.montage) {
                is MontageState.Error -> Text("Error")

                is MontageState.Success -> {
                    VideoPlayer(
                        file = state.montage.file,
                        modifier = Modifier
                            .size(300.dp, 400.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }

                MontageState.Idle -> {
                    Text("Idle")
                }

                MontageState.Making -> LoadingIndicator()
            }
        }
    }
}