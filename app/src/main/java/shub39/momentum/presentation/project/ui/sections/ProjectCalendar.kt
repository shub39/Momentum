/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.momentum.presentation.project.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.yearMonth
import com.skydoves.landscapist.coil3.CoilImage
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import shub39.momentum.R
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.project.ProjectState
import shub39.momentum.presentation.shared.MomentumTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectCalendar(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToDayInfo: (Long) -> Unit,
) {
    val calendarState =
        rememberCalendarState(
            startMonth = YearMonth.of(2025, 1),
            endMonth = YearMonth.now(),
            firstVisibleMonth = YearMonth.now(),
            outDateStyle = OutDateStyle.EndOfRow,
        )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(text = state.project?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { padding ->
        val today = LocalDate.now()
        VerticalCalendar(
            reverseLayout = true,
            state = calendarState,
            contentPadding =
                PaddingValues(
                    top = padding.calculateTopPadding() + 16.dp,
                    bottom = padding.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            modifier = Modifier.fillMaxWidth(),
            monthHeader = { month ->
                val days =
                    state.days.count { LocalDate.ofEpochDay(it.date).yearMonth == month.yearMonth }

                Card(
                    colors =
                        CardDefaults.cardColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = CircleShape,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier =
                            Modifier
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                                .fillMaxWidth(),
                    ) {
                        Text(
                            text = month.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                            fontWeight = FontWeight.Bold,
                        )

                        Text(text = "$days/${month.yearMonth.lengthOfMonth()}")
                    }
                }
            },
            monthFooter = { Spacer(modifier = Modifier.height(16.dp)) },
            dayContent = { day ->
                if (day.position.name == "MonthDate") {
                    val possibleDay = day.date <= today

                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .clickable(enabled = possibleDay) {
                                    onNavigateToDayInfo(day.date.toEpochDay())
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        state.days
                            .find { it.date == day.date.toEpochDay() }
                            ?.let { foundDay ->
                                Box {
                                    CoilImage(
                                        previewPlaceholder =
                                            painterResource(R.drawable.ic_launcher_foreground),
                                        imageModel = { foundDay.image },
                                        failure = {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .matchParentSize()
                                                        .border(
                                                            width = 2.dp,
                                                            color = MaterialTheme.colorScheme.error,
                                                            shape = CircleShape,
                                                        )
                                            )
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .blur(2.dp),
                                    )

                                    Box(
                                        modifier =
                                            Modifier
                                                .size(38.dp)
                                                .background(
                                                    color =
                                                        MaterialTheme.colorScheme.surface.copy(
                                                            alpha = 0.5f
                                                        )
                                                )
                                                .clip(CircleShape)
                                                .blur(2.dp)
                                    )
                                }
                            }

                        Text(
                            text = day.date.dayOfMonth.toString(),
                            color =
                                if (possibleDay) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                },
                        )
                    }
                }
            },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            ProjectState(
                project =
                    Project(id = 1, title = "Sample Project", description = "A sample project"),
                days =
                    (0..10).map {
                        Day(
                            id = it.toLong(),
                            projectId = 1,
                            image = "",
                            comment = it.toString(),
                            date = LocalDate.now().minusDays(it.toLong()).toEpochDay(),
                            isFavorite = false,
                        )
                    },
            )
        )
    }

    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        ProjectCalendar(state = state, onAction = {}, onNavigateBack = {}, onNavigateToDayInfo = {})
    }
}
