package shub39.momentum.project.ui.sections

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.delay
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.AlarmData
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.MomentumDialog
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import shub39.momentum.project.ui.component.AlarmCard
import shub39.momentum.project.ui.component.CreateMontageButton
import shub39.momentum.project.ui.component.FavDayCard
import shub39.momentum.project.ui.component.WeeklyHorizontalCalendar
import java.time.LocalDate

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ProjectDetails(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMontage: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    if (state.project == null) {
        LoadingIndicator()
    } else {
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }

        var showGuide by remember { mutableStateOf(false) }

        LaunchedEffect(state.days) {
            delay(200)
            showGuide = state.days.isEmpty()
        }

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeFlexibleTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text(text = state.project.title) },
                    subtitle = { Text(text = state.project.description) },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }

                        IconButton(
                            onClick = { showEditDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            val today = LocalDate.now()
            val weekState = rememberWeekCalendarState(
                startDate = LocalDate.of(2025, 1, 1),
                endDate = today,
                firstVisibleWeekDate = today
            )

            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // weekly horizontal calendar
                item {
                    WeeklyHorizontalCalendar(
                        onNavigateToCalendar = onNavigateToCalendar,
                        weekState = weekState,
                        days = state.days,
                        onAction = onAction,
                        showGuide = showGuide,
                        modifier = Modifier
                            .animateContentSize()
                            .padding(horizontal = 16.dp)
                    )
                }

                // montage wait/ creator options
                item {
                    CreateMontageButton(
                        daysSize = state.days.size,
                        onNavigateToMontage = onNavigateToMontage,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // reminder options
                item {
                    AlarmCard(
                        project = state.project,
                        onAction = onAction,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                // favorite days
                if (state.days.none { it.isFavorite }) {
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 32.dp, vertical = 60.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PhotoLibrary,
                                contentDescription = "Favorite Images",
                                modifier = Modifier.size(100.dp)
                            )

                            Text(
                                text = stringResource(R.string.favourites_text),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    stickyHeader {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.favourites),
                                    style = MaterialTheme.typography.titleLargeEmphasized
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorites",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        )
                    }

                    items(state.days.filter { it.isFavorite }, key = { it.id }) { day ->
                        FavDayCard(
                            day = day,
                            onClick = { onAction(ProjectAction.OnUpdateSelectedDay(day.date)) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }

        if (showEditDialog) {
            var newProjectTitle by remember { mutableStateOf(state.project.title) }
            var newProjectDescription by remember { mutableStateOf(state.project.description) }

            MomentumDialog(
                onDismissRequest = { showEditDialog = false }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )

                    Text(
                        text = stringResource(R.string.edit_project),
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = newProjectTitle,
                        onValueChange = { newProjectTitle = it },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        isError = newProjectTitle.length >= 20,
                        placeholder = { Text(text = stringResource(R.string.title)) },
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newProjectDescription,
                        onValueChange = { newProjectDescription = it },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        isError = newProjectDescription.length >= 100,
                        placeholder = { Text(text = stringResource(R.string.description)) },
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .fillMaxWidth()
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = {
                                onAction(
                                    ProjectAction.OnUpdateProject(
                                        project = state.project.copy(
                                            title = newProjectTitle.trim(),
                                            description = newProjectDescription.trim()
                                        )
                                    )
                                )
                                showEditDialog = false
                            },
                            enabled = newProjectTitle.length <= 20 &&
                                    newProjectDescription.length <= 100 &&
                                    newProjectTitle.isNotBlank() &&
                                    (newProjectTitle.trim() != state.project.title.trim() || newProjectDescription.trim() != state.project.description.trim()),
                            modifier = Modifier
                                .widthIn(max = 300.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.edit))
                        }

                        TextButton(
                            onClick = { showEditDialog = false }
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            MomentumDialog(
                onDismissRequest = { showDeleteDialog = false }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "Caution"
                    )

                    Text(
                        text = stringResource(R.string.delete_project),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = stringResource(R.string.delete_project_caution),
                        textAlign = TextAlign.Center
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier
                                .widthIn(max = 300.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }

                        TextButton(
                            onClick = {
                                onAction(ProjectAction.OnDeleteProject(state.project))
                                showDeleteDialog = false
                                onNavigateBack()
                            }
                        ) {
                            Text(text = stringResource(R.string.delete))
                        }
                    }
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
            ProjectState(
                project = Project(
                    id = 1,
                    title = "Sample Project",
                    description = "A sample project",
                    alarm = AlarmData(1L, emptyList())
                ),
                days = (0..5).map {
                    Day(
                        id = it.toLong(),
                        projectId = 1,
                        image = "",
                        comment = it.toString(),
                        date = LocalDate.now().minusDays(it.toLong()).toEpochDay(),
                        isFavorite = true
                    )
                }
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            seedColor = Color.Yellow,
            paletteStyle = PaletteStyle.Expressive
        )
    ) {
        ProjectDetails(
            state = state,
            onAction = {},
            onNavigateBack = {},
            onNavigateToMontage = {},
            onNavigateToCalendar = {}
        )
    }
}