package shub39.momentum.home.component

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import shub39.momentum.core.domain.data_classes.Project

@Composable
fun ProjectListItem(
    project: Project,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = project.title) },
        supportingContent = { Text(text = project.description) }
    )
}