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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import shub39.momentum.R
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageConfig
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.VideoQuality
import shub39.momentum.core.toFormatStyle
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.project.ProjectState
import shub39.momentum.presentation.shared.ColorPickerDialog
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.SettingSlider
import shub39.momentum.presentation.shared.zigZagBackground
import shub39.momentum.presentation.toDisplayString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MontageEditSheet(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onDismissRequest: () -> Unit,
    buttonEnabled: Boolean,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    var showColorPicker by remember { mutableStateOf(false) }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = state.montageConfig.backgroundColor,
            onSelect = {
                onAction(
                    ProjectAction.OnEditMontageConfig(
                        state.montageConfig.copy(backgroundColor = it)
                    )
                )
            },
            onDismiss = { showColorPicker = false },
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetGesturesEnabled = false,
        sheetState = sheetState,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.edit_montage),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = { onAction(ProjectAction.OnResetMontagePrefs) }) {
                Icon(painter = painterResource(R.drawable.sync), contentDescription = "Reset")
            }

            Button(
                onClick = {
                    onAction(ProjectAction.OnCreateMontage(state.days))
                    onDismissRequest()
                },
                enabled = buttonEnabled,
            ) {
                Text(text = stringResource(R.string.remake))
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = modifier
                .animateContentSize()
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // frames per image slider
            item {
                SettingSlider(
                    title = stringResource(R.string.frames_per_image),
                    value = state.montageConfig.framesPerImage.toFloat(),
                    onValueChange = {
                        onAction(
                            ProjectAction.OnEditMontageConfig(
                                state.montageConfig.copy(framesPerImage = it.roundToInt())
                            )
                        )
                    },
                    valueRange = 1f..10f,
                    steps = 8,
                    valueToShow = state.montageConfig.framesPerImage.toString(),
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            // fps slider
            item {
                SettingSlider(
                    title = stringResource(R.string.frames_per_sec),
                    value = state.montageConfig.framesPerSecond,
                    onValueChange = {
                        onAction(
                            ProjectAction.OnEditMontageConfig(
                                state.montageConfig.copy(framesPerSecond = it)
                            )
                        )
                    },
                    valueRange = 1f..10f,
                    steps = 8,
                    valueToShow = state.montageConfig.framesPerSecond.roundToInt().toString(),
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            // show date/ date style
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.show_date),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Switch(
                        checked = state.montageConfig.showDate,
                        onCheckedChange = {
                            onAction(
                                ProjectAction.OnEditMontageConfig(
                                    state.montageConfig.copy(showDate = it)
                                )
                            )
                        },
                    )
                }

                AnimatedVisibility(visible = state.montageConfig.showDate) {
                    FlowRow(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        DateStyle.entries.forEach { style ->
                            ToggleButton(
                                checked = state.montageConfig.dateStyle == style,
                                onCheckedChange = {
                                    onAction(
                                        ProjectAction.OnEditMontageConfig(
                                            state.montageConfig.copy(dateStyle = style)
                                        )
                                    )
                                },
                            ) {
                                Text(
                                    text =
                                        LocalDate.now()
                                            .format(
                                                DateTimeFormatter.ofLocalizedDate(
                                                    style.toFormatStyle()
                                                )
                                            )
                                )
                            }
                        }
                    }
                }
            }

            // show day message
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.show_message),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Switch(
                        checked = state.montageConfig.showMessage,
                        onCheckedChange = {
                            onAction(
                                ProjectAction.OnEditMontageConfig(
                                    state.montageConfig.copy(showMessage = it)
                                )
                            )
                        },
                    )
                }
            }

            // stabilize faces
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.stabilize_faces),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Switch(
                        checked = state.montageConfig.stabilizeFaces,
                        onCheckedChange = {
                            if (isPlusUser) {
                                onAction(
                                    ProjectAction.OnEditMontageConfig(
                                        state.montageConfig.copy(stabilizeFaces = it)
                                    )
                                )
                            } else {
                                onNavigateToPaywall()
                            }
                        },
                    )
                }

                AnimatedVisibility(
                    visible = state.montageConfig.stabilizeFaces,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.censor_faces),
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Switch(
                            checked = state.montageConfig.censorFaces,
                            enabled = state.montageConfig.stabilizeFaces,
                            onCheckedChange = {
                                if (isPlusUser) {
                                    onAction(
                                        ProjectAction.OnEditMontageConfig(
                                            state.montageConfig.copy(censorFaces = it)
                                        )
                                    )
                                } else {
                                    onNavigateToPaywall()
                                }
                            },
                        )
                    }
                }
            }

            if (!isPlusUser) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .zigZagBackground(),
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToPaywall) {
                            Text(text = stringResource(R.string.unlock_more_pro))
                        }
                    }
                }
            }

            // video quality
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.video_quality),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.size(44.dp))
                }

                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VideoQuality.entries.forEach { quality ->
                        ToggleButton(
                            checked = quality == state.montageConfig.videoQuality,
                            enabled = isPlusUser,
                            onCheckedChange = {
                                if (isPlusUser) {
                                    onAction(
                                        ProjectAction.OnEditMontageConfig(
                                            state.montageConfig.copy(videoQuality = quality)
                                        )
                                    )
                                } else {
                                    onNavigateToPaywall()
                                }
                            },
                        ) {
                            Text(text = quality.name)
                        }
                    }
                }
            }

            // text font
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.video_font),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.size(44.dp))
                }

                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Fonts.entries.forEach { font ->
                        ToggleButton(
                            checked = font == state.montageConfig.font,
                            onCheckedChange = {
                                onAction(
                                    ProjectAction.OnEditMontageConfig(
                                        state.montageConfig.copy(font = font)
                                    )
                                )
                            },
                            enabled = isPlusUser,
                        ) {
                            Text(text = font.toDisplayString())
                        }
                    }
                }
            }

            // show watermark
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.show_watermark),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Switch(
                        checked = state.montageConfig.waterMark,
                        enabled = isPlusUser,
                        onCheckedChange = {
                            if (isPlusUser) {
                                onAction(
                                    ProjectAction.OnEditMontageConfig(
                                        state.montageConfig.copy(waterMark = it)
                                    )
                                )
                            } else {
                                onNavigateToPaywall()
                            }
                        },
                    )
                }
            }

            // set background color
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.background_color),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    IconButton(
                        onClick = {
                            if (isPlusUser) {
                                showColorPicker = true
                            } else {
                                onNavigateToPaywall()
                            }
                        },
                        enabled = isPlusUser,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = state.montageConfig.backgroundColor,
                                contentColor = contentColorFor(state.montageConfig.backgroundColor),
                            ),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = "Pick color",
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun Preview() {
    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        MontageEditSheet(
            state =
                ProjectState(
                    days =
                        listOf(
                            Day(
                                id = 1,
                                projectId = 1,
                                image = "",
                                comment = "",
                                date = 1,
                                isFavorite = false,
                            )
                        ),
                    montageConfig = MontageConfig(stabilizeFaces = true),
                ),
            onAction = {},
            buttonEnabled = false,
            onDismissRequest = {},
            sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded),
            isPlusUser = true,
            onNavigateToPaywall = {},
        )
    }
}
