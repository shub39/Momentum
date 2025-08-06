package shub39.momentum.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.home.HomeAction
import shub39.momentum.home.HomeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectList(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProject: () -> Unit,
    onNavigateToNewProject: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                title = { Text(text = stringResource(R.string.app_name)) },
                subtitle = {
                    Text(
                        text = "${state.projects.size} " + stringResource(
                            R.string.projects
                        )
                    )
                },
                navigationIcon = {
                    FilledTonalIconToggleButton(
                        checked = state.sendNotifications,
                        onCheckedChange = {
                            onAction(
                                HomeAction.OnChangeNotificationPref(
                                    it
                                )
                            )
                        },
                        shapes = IconToggleButtonShapes(
                            shape = CircleShape,
                            pressedShape = RoundedCornerShape(10.dp),
                            checkedShape = CircleShape
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewProject
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Project"
                )
            }
        }
    ) { padding ->
        if (state.projects.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.projects, key = { it.project.id }) { projectListData ->
                    ProjectListItem(
                        projectListData = projectListData,
                        modifier = Modifier.clickable {
                            onAction(HomeAction.OnChangeProject(projectListData.project))
                            onNavigateToProject()
                        }
                    )
                }
            }
        } else {
            Empty()
        }
    }
}