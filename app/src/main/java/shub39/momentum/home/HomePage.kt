package shub39.momentum.home

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.home.component.Empty
import shub39.momentum.home.component.ProjectListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomePage(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProject: () -> Unit
) {
    var projectAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                title = { Text(text = stringResource(R.string.app_name)) },
                subtitle = { Text(text = "${state.projects.size} " + stringResource(R.string.projects)) },
                navigationIcon = {
                    FilledTonalIconToggleButton(
                        checked = state.sendNotifications,
                        onCheckedChange = { onAction(HomeAction.OnChangeNotificationPref(it)) },
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
                onClick = { projectAddSheet = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Project"
                )
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = state.isLoading,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { loading ->
            if (loading) {
                LoadingIndicator()
            } else {
                if (state.projects.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.projects, key = { it.id }) { project ->
                            ProjectListItem(
                                project = project,
                                modifier = Modifier.clickable {
                                    onAction(HomeAction.OnChangeProject(project))
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

        if (projectAddSheet) {
            // TODO: Make it prettier

            var newProjectTitle by remember { mutableStateOf("") }
            var newProjectDescription by remember { mutableStateOf("") }

            ModalBottomSheet(
                onDismissRequest = { projectAddSheet = false }
            ) {
                Text(text = stringResource(R.string.add_new_project))

                OutlinedTextField(
                    value = newProjectTitle,
                    onValueChange = { newProjectTitle = it }
                )

                OutlinedTextField(
                    value = newProjectDescription,
                    onValueChange = { newProjectDescription = it }
                )

                Button(
                    enabled = newProjectDescription.isNotBlank() && newProjectTitle.isNotBlank(),
                    onClick = {
                        onAction(
                            HomeAction.OnAddProject(
                                title = newProjectTitle,
                                description = newProjectDescription
                            )
                        )

                        projectAddSheet = false
                    }
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            HomeState(
                isLoading = false,
                projects = (0..10).map {
                    Project(
                        id = it.toLong(),
                        title = "Project $it",
                        description = "Description for project $it",
                        startDate = it.toLong(),
                        lastUpdatedDate = it.toLong()
                    )
                }
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            font = Fonts.MANROPE,
            paletteStyle = PaletteStyle.TonalSpot
        )
    ) {
        HomePage(
            state = state,
            onAction = {},
            onNavigateToSettings = {},
            onNavigateToProject = {}
        )
    }
}