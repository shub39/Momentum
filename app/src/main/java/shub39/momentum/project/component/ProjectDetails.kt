package shub39.momentum.project.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.materialkolor.PaletteStyle
import com.skydoves.landscapist.coil3.CoilImage
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
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
    AnimatedContent(
        targetState = state.project,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { project ->
        if (project == null) {
            LoadingIndicator()
        } else {
            Scaffold(
                topBar = {
                    LargeFlexibleTopAppBar(
                        title = { Text(text = project.title) },
                        subtitle = { Text(text = project.description) },
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
                                onClick = { /*TODO: Delete Logic*/ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }

                            IconButton(
                                onClick = { /*TODO: Edit Logic*/ }
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
                    startDate = today.minusMonths(12),
                    endDate = today,
                    firstVisibleWeekDate = today
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // weekly horizontal calendar
                    item {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
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

                            WeekCalendar(
                                state = weekState,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                dayContent = { weekDay ->
                                    val day =
                                        state.days.find { it.date == weekDay.date.toEpochDay() }

                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        day?.let {
                                            CoilImage(
                                                imageModel = { it.image },
                                                modifier = Modifier
                                                    .matchParentSize()
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
                                                fontWeight = FontWeight.Bold,
                                            )

                                            Text(
                                                text = weekDay.date.dayOfWeek.toString().take(3),
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = onNavigateToMontage,
                            enabled = state.days.isNotEmpty()
                        ) {
                            Text("Montage")
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
                    startDate = 1,
                    lastUpdatedDate = 1
                )
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            paletteStyle = PaletteStyle.Fidelity
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