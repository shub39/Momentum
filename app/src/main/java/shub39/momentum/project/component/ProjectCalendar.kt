package shub39.momentum.project.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.skydoves.landscapist.coil3.CoilImage
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectCalendar(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val calendarState = rememberCalendarState(
        startMonth = YearMonth.of(2025, 1),
        endMonth = YearMonth.now(),
        firstVisibleMonth = YearMonth.now(),
        outDateStyle = OutDateStyle.EndOfRow
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.project?.title!!) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        val today = LocalDate.now()
        VerticalCalendar(
            reverseLayout = true,
            state = calendarState,
            contentPadding = padding,
            modifier = Modifier.padding(horizontal = 16.dp),
            monthHeader = { month ->
                Card(
                    colors = CardDefaults.cardColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = CircleShape
                ) {
                    Text(
                        text = month.yearMonth.format(
                            DateTimeFormatter.ofPattern("MMMM yyyy")
                        ),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                    )
                }
            },
            monthFooter = { Spacer(modifier = Modifier.height(16.dp)) },
            dayContent = { day ->
                if (day.position.name == "MonthDate") {
                    val possibleDay = day.date <= today

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(enabled = possibleDay) {
                                onAction(ProjectAction.OnUpdateSelectedDay(day.date.toEpochDay()))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        state.days.find { it.date == day.date.toEpochDay() }?.let { foundDay ->
                            Box {
                                CoilImage(
                                    previewPlaceholder = painterResource(R.drawable.ic_launcher_foreground),
                                    imageModel = { foundDay.image },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .blur(2.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                        )
                                        .clip(CircleShape)
                                        .blur(2.dp)
                                )
                            }
                        }

                        Text(
                            text = day.date.dayOfMonth.toString(),
                            color = if (possibleDay) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            }
                        )
                    }
                }
            }
        )
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
                days = (0..10).map {
                    Day(
                        id = it.toLong(),
                        projectId = 1,
                        image = "",
                        comment = it.toString(),
                        date = LocalDate.now().minusDays(it.toLong()).toEpochDay(),
                        isFavorite = false
                    )
                }
            )
        )
    }

    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        ProjectCalendar(
            state = state,
            onAction = {},
            onNavigateBack = {}
        )
    }
}