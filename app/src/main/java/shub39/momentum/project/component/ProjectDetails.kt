package shub39.momentum.project.component

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.skydoves.landscapist.coil3.CoilImage
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.uri
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetails(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMontage: () -> Unit
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
                AnimatedVisibility(
                    visible = state.days.isNotEmpty()
                ) {
                    FloatingActionButton(
                        onClick = {
                            onNavigateToMontage()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Show Preview"
                        )
                    }
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
                                if (state.days.map { it.date }.contains(day.date.toEpochDay())) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable(
                                enabled = day.date < LocalDate.now()
                            ) { selectedDate = day.date.toEpochDay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day.date.dayOfMonth.toString())
                    }
                }
            )
        }

        if (selectedDate != null) {
            val context = LocalContext.current
            val day = state.days.find { it.date == selectedDate }
            var imageFile: PlatformFile? by remember {
                mutableStateOf(
                    day?.let { PlatformFile(it.image.toUri()) }
                )
            }

            val imagePicker = rememberFilePickerLauncher(
                type = FileKitType.Image
            ) { image ->
                if (image != null) {
                    imageFile = image
                    context.contentResolver.takePersistableUriPermission(
                        image.uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }

            ModalBottomSheet(
                onDismissRequest = { selectedDate = null }
            ) {
                CoilImage(
                    imageModel = { imageFile?.uri },
                    loading = { LoadingIndicator() },
                    failure = { Text(text = it.reason?.message.toString()) }
                )

                Text(text = selectedDate.toString())

                if (day == null) {
                    Button(
                        onClick = { imagePicker.launch() },
                    ) { Text("Select Image") }

                    Button(
                        onClick = {
                            onAction(
                                ProjectAction.OnUpsertDay(
                                    Day(
                                        projectId = state.project.id,
                                        image = imageFile!!.uri.toString(),
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
                } else {
                    Button(
                        onClick = {
                            onAction(ProjectAction.OnDeleteDay(day))
                            selectedDate = null
                        }
                    ) {
                        Text("Delete Day")
                    }
                }
            }
        }
    }
}