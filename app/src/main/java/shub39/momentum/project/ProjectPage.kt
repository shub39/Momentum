package shub39.momentum.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import shub39.momentum.core.domain.data_classes.Day
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectPage(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedDate: Long? by remember { mutableStateOf(null) }

    if (state.project == null) {
        LoadingIndicator()
    } else {
        val calendarState = rememberCalendarState(
            startMonth = YearMonth.now().minusMonths(12),
            endMonth = YearMonth.now(),
            firstVisibleMonth = YearMonth.now()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = state.project.title) },
                    subtitle = { Text(text = state.project.description) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        // TODO: Navigate to video maker page
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Show Preview"
                    )
                }
            }
        ) { padding ->
            VerticalCalendar(
                reverseLayout = true,
                state = calendarState,
                modifier = Modifier.padding(padding),
                monthHeader = { month ->
                    Text(text = month.yearMonth.toString())
                },
                dayContent = { day ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (day.date in state.dates) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable { selectedDate = day.date.toEpochDay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day.date.dayOfMonth.toString())
                    }
                }
            )
        }

        if (selectedDate != null) {
            var imageFile: PlatformFile? by remember { mutableStateOf(null) }
            val photoPicker = rememberFilePickerLauncher(
                type = FileKitType.Image
            ) { image -> imageFile = image }

            ModalBottomSheet(
                onDismissRequest = { selectedDate = null }
            ) {
                Text(text = selectedDate.toString())
                Button(
                    onClick = { photoPicker.launch() }
                ) { Text("Select Image") }
                Button(
                    onClick = {
                        onAction(
                            ProjectAction.OnUpsertDay(
                                Day(
                                    projectId = state.project.id,
                                    image = imageFile!!.toString(),
                                    comment = null,
                                    date = selectedDate!!,
                                    isFavorite = false
                                )
                            )
                        )
                        selectedDate = null
                    },
                    enabled = imageFile != null
                ) { Text("Add Day") }
            }
        }
    }
}