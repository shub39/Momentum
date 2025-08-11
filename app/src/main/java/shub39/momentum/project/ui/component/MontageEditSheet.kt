package shub39.momentum.project.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.presentation.ColorPickerDialog
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.core.presentation.SettingSlider
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MontageEditSheet(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onDismissRequest: () -> Unit,
    buttonEnabled: Boolean,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            onDismiss = { showColorPicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.edit_montage),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    onAction(ProjectAction.OnCreateMontage(state.days))
                    onDismissRequest()
                },
                enabled = buttonEnabled
            ) {
                Text(
                    text = stringResource(R.string.remake)
                )
            }
        }

        Column(
            modifier = modifier
                .animateContentSize()
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                valueToShow = state.montageConfig.framesPerImage.toString()
            )

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
                valueToShow = state.montageConfig.framesPerSecond.roundToInt().toString()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.show_date),
                    style = MaterialTheme.typography.titleLarge
                )

                Switch(
                    checked = state.montageConfig.showDate,
                    onCheckedChange = {
                        onAction(
                            ProjectAction.OnEditMontageConfig(
                                state.montageConfig.copy(showDate = it)
                            )
                        )
                    }
                )
            }

            AnimatedVisibility(visible = state.montageConfig.showDate) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormatStyle.entries.forEach { style ->
                        ToggleButton(
                            checked = state.montageConfig.dateStyle == style,
                            onCheckedChange = {
                                onAction(
                                    ProjectAction.OnEditMontageConfig(
                                        state.montageConfig.copy(
                                            dateStyle = style
                                        )
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = LocalDate.now()
                                    .format(DateTimeFormatter.ofLocalizedDate(style))
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.show_watermark),
                    style = MaterialTheme.typography.titleLarge
                )

                Switch(
                    checked = state.montageConfig.waterMark,
                    onCheckedChange = {
                        onAction(
                            ProjectAction.OnEditMontageConfig(
                                state.montageConfig.copy(waterMark = it)
                            )
                        )
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.show_message),
                    style = MaterialTheme.typography.titleLarge
                )

                Switch(
                    checked = state.montageConfig.showMessage,
                    onCheckedChange = {
                        onAction(
                            ProjectAction.OnEditMontageConfig(
                                state.montageConfig.copy(showMessage = it)
                            )
                        )
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.background_color),
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = { showColorPicker = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = state.montageConfig.backgroundColor,
                        contentColor = contentColorFor(state.montageConfig.backgroundColor)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Pick color"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun Preview() {
    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        MontageEditSheet(
            state = ProjectState(
                days = listOf(
                    Day(
                        id = 1,
                        projectId = 1,
                        image = "",
                        comment = "",
                        date = 1,
                        isFavorite = false
                    )
                )
            ),
            onAction = {},
            buttonEnabled = false,
            onDismissRequest = {},
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            )
        )
    }
}