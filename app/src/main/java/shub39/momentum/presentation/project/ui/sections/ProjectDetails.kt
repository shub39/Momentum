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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import java.time.LocalDate
import kotlinx.coroutines.delay
import shub39.momentum.R
import shub39.momentum.core.data_classes.AlarmData
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.PaletteStyle
import shub39.momentum.presentation.home.ui.sections.ProjectUpsertSheet
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.project.ProjectState
import shub39.momentum.presentation.project.ScanState
import shub39.momentum.presentation.project.ui.component.AlarmCard
import shub39.momentum.presentation.project.ui.component.CreateMontageButton
import shub39.momentum.presentation.project.ui.component.FavDayCard
import shub39.momentum.presentation.project.ui.component.WeeklyHorizontalCalendar
import shub39.momentum.presentation.shared.MomentumDialog
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.flexFontEmphasis
import shub39.momentum.presentation.shared.flexFontRounded

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun ProjectDetails(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMontage: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToDayInfo: (Long) -> Unit,
) {
    if (state.project == null) {
        LoadingIndicator()
    } else {
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }
        var showRescanDialog by rememberSaveable { mutableStateOf(false) }

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
                    title = { Text(text = state.project.title, fontFamily = flexFontEmphasis()) },
                    subtitle = {
                        Text(text = state.project.description, fontFamily = flexFontRounded())
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showRescanDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.detect_face),
                                contentDescription = null,
                            )
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.delete),
                                contentDescription = "Delete",
                            )
                        }

                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.edit),
                                contentDescription = "Edit",
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            val today = LocalDate.now()
            val weekState =
                rememberWeekCalendarState(
                    startDate = today.minusYears(1),
                    endDate = today,
                    firstVisibleWeekDate = today,
                )

            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 60.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // weekly horizontal calendar
                item {
                    WeeklyHorizontalCalendar(
                        onNavigateToCalendar = onNavigateToCalendar,
                        weekState = weekState,
                        days = state.days,
                        onNavigateToDayInfo = onNavigateToDayInfo,
                        showGuide = showGuide,
                        shape =
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 28.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 4.dp,
                            ),
                        modifier = Modifier.animateContentSize().padding(horizontal = 16.dp),
                    )
                }

                // reminder options
                item {
                    AlarmCard(
                        project = state.project,
                        onAction = onAction,
                        shape =
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                                bottomStart = 28.dp,
                                bottomEnd = 28.dp,
                            ),
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                // montage wait/ creator options
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    CreateMontageButton(
                        daysSize = state.days.size,
                        onNavigateToMontage = onNavigateToMontage,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                // favorite days
                if (state.days.none { it.isFavorite }) {
                    item {
                        Column(
                            modifier =
                                Modifier.padding(horizontal = 32.dp, vertical = 60.dp)
                                    .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier =
                                    Modifier.size(48.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialShapes.Sunny.toShape(),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.photo_library),
                                    contentDescription = "Favorites",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }

                            Text(
                                text = stringResource(R.string.favourites_text),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    stickyHeader {
                        Row(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(
                                        top = 32.dp,
                                        bottom = 16.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                    ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier.size(48.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialShapes.Sunny.toShape(),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.favorite),
                                    contentDescription = "Favorites",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }

                            Text(
                                text = stringResource(R.string.favourites),
                                style = MaterialTheme.typography.titleLargeEmphasized,
                            )
                        }
                    }

                    val favoriteDays = state.days.filter { it.isFavorite }
                    itemsIndexed(favoriteDays, key = { _, it -> it.id }) { index, day ->
                        val shape =
                            when {
                                favoriteDays.size == 1 -> RoundedCornerShape(32.dp)
                                index == 0 ->
                                    RoundedCornerShape(
                                        topStart = 32.dp,
                                        topEnd = 32.dp,
                                        bottomStart = 4.dp,
                                        bottomEnd = 4.dp,
                                    )

                                index == favoriteDays.size - 1 ->
                                    RoundedCornerShape(
                                        topStart = 4.dp,
                                        topEnd = 4.dp,
                                        bottomStart = 32.dp,
                                        bottomEnd = 32.dp,
                                    )

                                else -> RoundedCornerShape(4.dp)
                            }

                        FavDayCard(
                            day = day,
                            onClick = { onNavigateToDayInfo(day.date) },
                            modifier = Modifier.padding(horizontal = 16.dp),
                            shape = shape,
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }

        if (showRescanDialog) {
            MomentumDialog(
                onDismissRequest = {
                    if (state.scanState !is ScanState.Processing) {
                        showRescanDialog = false
                    }
                }
            ) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier.size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialShapes.Pill.toShape(),
                                ),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.detect_face),
                            contentDescription = null,
                        )
                    }

                    Text(
                        text = stringResource(R.string.rescan_faces),
                        style =
                            MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = flexFontEmphasis()
                            ),
                    )

                    Text(
                        text = stringResource(R.string.rescan_faces_desc),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    AnimatedVisibility(visible = state.scanState is ScanState.Processing) {
                        LinearWavyProgressIndicator(
                            progress = {
                                (state.scanState as? ScanState.Processing)?.progress ?: 0f
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = { onAction(ProjectAction.OnStartFaceScan) },
                            enabled = state.scanState is ScanState.Idle,
                        ) {
                            Text(text = stringResource(R.string.start_scan))
                        }

                        Spacer(modifier = Modifier.widthIn(4.dp))

                        Button(
                            onClick = {
                                onAction(ProjectAction.OnResetScanState)
                                showRescanDialog = false
                            },
                            enabled = state.scanState is ScanState.Done,
                        ) {
                            Text(text = stringResource(R.string.done))
                        }
                    }
                }
            }
        }

        if (showEditDialog) {
            ProjectUpsertSheet(
                project = state.project,
                onUpsertProject = { onAction(ProjectAction.OnUpdateProject(it)) },
                edit = true,
                onDismissRequest = { showEditDialog = false },
            )
        }

        if (showDeleteDialog) {
            MomentumDialog(onDismissRequest = { showDeleteDialog = false }) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier.size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialShapes.Pill.toShape(),
                                ),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.warning),
                            contentDescription = "Caution",
                        )
                    }

                    Text(
                        text = stringResource(R.string.delete_project),
                        style =
                            MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = flexFontEmphasis()
                            ),
                    )

                    Text(
                        text = stringResource(R.string.delete_project_caution),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { showDeleteDialog = false }) {
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
                project =
                    Project(
                        id = 1,
                        title = "Sample Project",
                        description = "A sample project",
                        alarm = AlarmData(1L, emptyList()),
                    ),
                days =
                    (0..5).map {
                        Day(
                            id = it.toLong(),
                            projectId = 1,
                            image = "",
                            comment = it.toString(),
                            date = LocalDate.now().minusDays(it.toLong()).toEpochDay(),
                            isFavorite = true,
                        )
                    },
            )
        )
    }

    MomentumTheme(
        theme =
            Theme(
                appTheme = AppTheme.DARK,
                seedColor = Color.Yellow,
                paletteStyle = PaletteStyle.EXPRESSIVE,
            )
    ) {
        ProjectDetails(
            state = state,
            onAction = {},
            onNavigateBack = {},
            onNavigateToMontage = {},
            onNavigateToCalendar = {},
            onNavigateToDayInfo = {},
        )
    }
}
