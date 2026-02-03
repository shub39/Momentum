package shub39.momentum.home.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import shub39.momentum.R
import shub39.momentum.domain.data_classes.Day
import shub39.momentum.domain.data_classes.Project
import shub39.momentum.domain.data_classes.ProjectListData
import shub39.momentum.domain.data_classes.Theme
import shub39.momentum.domain.enums.AppTheme
import shub39.momentum.domain.enums.Fonts
import shub39.momentum.home.HomeAction
import shub39.momentum.home.HomeState
import shub39.momentum.home.ui.component.Empty
import shub39.momentum.home.ui.component.ProjectListItem
import shub39.momentum.presentation.MomentumTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectList(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProject: () -> Unit,
    onNavigateToNewProject: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.app_name)) },
                subtitle = {
                    Text(
                        text = "${state.projects.size} " + stringResource(R.string.projects)
                    )
                },
                actions = {
                    IconButton(
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
            MediumFloatingActionButton(
                onClick = onNavigateToNewProject
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Project",
                    modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize)
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
                        onClick = {
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

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            HomeState(
                projects = (0..10).map {
                    ProjectListData(
                        project = Project(
                            id = it.toLong(),
                            title = "Project $it",
                            description = "Description for project $it",
                        ),
                        last10Days = (0..10).map { it1 ->
                            Day(
                                id = it1.toLong(),
                                projectId = it1.toLong(),
                                image = "",
                                comment = "",
                                date = LocalDate.now().toEpochDay(),
                                isFavorite = false
                            )
                        }
                    )
                }
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            seedColor = Color.Yellow,
            appTheme = AppTheme.DARK,
            font = Fonts.FIGTREE,
            paletteStyle = PaletteStyle.Fidelity
        )
    ) {
        ProjectList(
            state = state,
            onAction = {},
            onNavigateToSettings = {},
            onNavigateToProject = {},
            onNavigateToNewProject = {}
        )
    }
}