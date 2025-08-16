package shub39.momentum.project.ui.sections

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.materialkolor.PaletteStyle
import com.skydoves.landscapist.coil3.CoilImage
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.MomentumDialog
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import shub39.momentum.project.ui.component.AlarmCard
import shub39.momentum.project.ui.component.FavDayCard
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // weekly horizontal calendar
                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.calendar),
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                onClick = onNavigateToCalendar
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Calendar"
                                )
                            }
                        }

                        val today = LocalDate.now()
                        WeekCalendar(
                            state = weekState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            dayContent = { weekDay ->
                                val day =
                                    state.days.find { it.date == weekDay.date.toEpochDay() }
                                val possibleDay = weekDay.date <= today

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .clickable(enabled = possibleDay) {
                                            onAction(ProjectAction.OnUpdateSelectedDay(weekDay.date.toEpochDay()))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    day?.let {
                                        CoilImage(
                                            imageModel = { it.image },
                                            failure = {
                                                Box(
                                                    modifier = Modifier
                                                        .matchParentSize()
                                                        .border(
                                                            width = 2.dp,
                                                            color = MaterialTheme.colorScheme.error,
                                                            shape = CircleShape
                                                        )
                                                )
                                            },
                                            modifier = Modifier
                                                .matchParentSize()
                                                .blur(2.dp)
                                                .clip(CircleShape)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .background(
                                                    color = MaterialTheme.colorScheme.background.copy(
                                                        alpha = 0.5f
                                                    ),
                                                    shape = CircleShape
                                                )
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = weekDay.date.dayOfMonth.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (possibleDay) {
                                                MaterialTheme.colorScheme.onBackground
                                            } else {
                                                MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = 0.5f
                                                )
                                            },
                                            fontWeight = FontWeight.Bold,
                                        )

                                        Text(
                                            text = weekDay.date.dayOfWeek.toString().take(3),
                                            color = if (possibleDay) {
                                                MaterialTheme.colorScheme.onBackground
                                            } else {
                                                MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = 0.5f
                                                )
                                            },
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                // montage wait/ creator options
                item {
                    val canCreateMontage = state.days.size >= 5
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = canCreateMontage) {
                                onNavigateToMontage()
                            },
                        shape = MaterialTheme.shapes.large,
                        colors = if (canCreateMontage) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.montage),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                if (!canCreateMontage) {
                                    val daysLeft = 5 - state.days.size
                                    Text(
                                        text = pluralStringResource(
                                            id = R.plurals.add_more_days,
                                            count = daysLeft,
                                            formatArgs = arrayOf(daysLeft)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Box(
                                modifier = Modifier.wrapContentSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (canCreateMontage) {
                                    val infiniteTransition =
                                        rememberInfiniteTransition(label = "rotation")

                                    val rotation by infiniteTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 360f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(
                                                durationMillis = 2000,
                                                easing = LinearEasing
                                            ),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "infinite rotation"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .graphicsLayer {
                                                rotationZ = rotation
                                            }
                                            .background(
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                shape = VerySunny.toShape()
                                            )
                                    )

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Create Montage",
                                        tint = MaterialTheme.colorScheme.primaryContainer
                                    )
                                } else {
                                    CircularWavyProgressIndicator(
                                        progress = { state.days.size.toFloat() / 5 },
                                        modifier = Modifier.size(50.dp)
                                    )

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Create Montage"
                                    )
                                }
                            }

                        }
                    }
                }

                // reminder options
                item { AlarmCard(state.project, onAction) }

                item { HorizontalDivider() }

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
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.favourites),
                                style = MaterialTheme.typography.displaySmall
                            )

                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorites",
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(40.dp)
                            )
                        }
                    }

                    items(state.days.filter { it.isFavorite }, key = { it.id }) { day ->
                        FavDayCard(
                            day = day,
                            onClick = {
                                onAction(ProjectAction.OnUpdateSelectedDay(day.date))
                            }
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
                ),
                days = (0..3).map {
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