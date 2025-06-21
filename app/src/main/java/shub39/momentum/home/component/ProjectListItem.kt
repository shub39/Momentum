package shub39.momentum.home.component

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import shub39.momentum.core.domain.data_classes.Project

@Composable
fun ProjectListItem(
    project: Project,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
            supportingColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        headlineContent = { Text(text = project.title) },
        supportingContent = { Text(text = project.description) }
    )
}