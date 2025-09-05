package shub39.momentum.project.ui.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState

@Composable
fun ProjectShorts(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    modifier: Modifier = Modifier
) {

}

@Preview
@Composable
private fun Preview() {
    ProjectShorts(
        state = ProjectState(),
        onAction = {}
    )
}