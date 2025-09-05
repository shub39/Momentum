package shub39.momentum.project.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.skydoves.landscapist.coil3.CoilImage
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.project.ProjectAction
import java.time.LocalDate

@Composable
fun WeeklyHorizontalCalendar(
    onNavigateToCalendar: () -> Unit,
    weekState: WeekCalendarState,
    days: List<Day>,
    onAction: (ProjectAction) -> Unit,
    showGuide: Boolean
) {
    OutlinedCard(
        modifier = Modifier
            .animateContentSize()
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
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
                val day = days.find { it.date == weekDay.date.toEpochDay() }
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

        if (showGuide) {
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.guide_text),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}