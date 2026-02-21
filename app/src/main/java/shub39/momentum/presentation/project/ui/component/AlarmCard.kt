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
package shub39.momentum.presentation.project.ui.component

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import shub39.momentum.R
import shub39.momentum.core.data_classes.AlarmData
import shub39.momentum.core.data_classes.Project
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.shared.MomentumTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlarmCard(project: Project, onAction: (ProjectAction) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) showDialog = true
            else
                Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }

    val contentColor = MaterialTheme.colorScheme.onSurface
    val containerColor by
    animateColorAsState(
        targetValue =
            if (project.alarm != null) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
    )

    val roundness by animateDpAsState(targetValue = if (project.alarm != null) 16.dp else 100.dp)

    Card(
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth(),
        colors =
            CardDefaults.cardColors(contentColor = contentColor, containerColor = containerColor),
        shape = RoundedCornerShape(roundness),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.set_reminder),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )

                project.alarm?.let {
                    Text(
                        text =
                            stringResource(R.string.everyday_at) +
                                    " ${
                            LocalTime.ofSecondOfDay(it.time).format(
                                DateTimeFormatter.ofLocalizedTime(
                                    FormatStyle.SHORT
                                )
                            )
                        }",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            Switch(
                checked = project.alarm != null,
                onCheckedChange = { checked ->
                    if (checked) {
                        if (
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS,
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            showDialog = true
                        }
                    } else {
                        onAction(ProjectAction.OnUpdateReminder())
                    }
                },
            )
        }
    }

    if (showDialog) {
        val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0)

        TimePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAction(
                            ProjectAction.OnUpdateReminder(
                                AlarmData(
                                    time =
                                        LocalTime.of(timePickerState.hour, timePickerState.minute)
                                            .toSecondOfDay()
                                            .toLong(),
                                    days = DayOfWeek.entries.map { it.name },
                                )
                            )
                        )
                        showDialog = false
                    },
                ) {
                    Text(stringResource(R.string.done))
                }
            },
            title = {},
        ) {
            TimeInput(state = timePickerState)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MomentumTheme {
        AlarmCard(
            project =
                Project(
                    id = 1,
                    title = "Project1",
                    description = "Desc",
                    alarm = AlarmData(time = 1L, days = listOf()),
                ),
            onAction = {},
        )
    }
}
