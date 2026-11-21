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
package shub39.momentum.presentation.settings.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.core.backup.ExportState
import shub39.momentum.core.backup.ImportState
import shub39.momentum.presentation.settings.SettingsAction
import shub39.momentum.presentation.settings.SettingsState
import shub39.momentum.presentation.shared.ExpressiveTopBar
import shub39.momentum.presentation.shared.LazierColumn
import shub39.momentum.presentation.shared.LazierDefaults
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.flexFontEmphasis
import shub39.momentum.presentation.shared.leadingItemShape
import shub39.momentum.presentation.shared.listItemColors
import shub39.momentum.presentation.shared.rectListItemShapes

@Composable
fun Backup(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ExpressiveTopBar(
                title = {
                    Text(text = stringResource(R.string.backup), fontFamily = flexFontEmphasis())
                },
                navigationIcon = {
                    FilledTonalIconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.nav_arrow_back),
                            contentDescription = "Navigate Back",
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazierColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = padding,
            extraPadding = LazierDefaults.pagePadding(extraBottom = 44.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Column(modifier = Modifier.clip(leadingItemShape())) {
                        ListItem(
                            content = { Text(text = stringResource(R.string.export)) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.export),
                                    contentDescription = null,
                                )
                            },
                            shapes = rectListItemShapes(),
                            colors = listItemColors(),
                            supportingContent = {
                                Text(
                                    text =
                                        "${stringResource(R.string.export_desc)}. ${stringResource(R.string.backup_caution)}"
                                )
                            },
                        )

                        Row(
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            Button(
                                onClick = { onAction(SettingsAction.OnExportData) },
                                enabled = state.exportState == ExportState.IDLE,
                                modifier = Modifier.weight(1f),
                            ) {
                                when (state.exportState) {
                                    IDLE ->
                                        Icon(
                                            painter = painterResource(R.drawable.play),
                                            contentDescription = "Start",
                                        )

                                    EXPORTING ->
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))

                                    EXPORTED ->
                                        Icon(
                                            painter = painterResource(R.drawable.check_circle),
                                            contentDescription = "Done",
                                        )

                                    FAILURE ->
                                        Icon(
                                            painter = painterResource(R.drawable.warning),
                                            contentDescription = "Fail",
                                        )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.clip(endItemShape())) {
                        ListItem(
                            colors = listItemColors(),
                            shapes = rectListItemShapes(),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.download),
                                    contentDescription = null,
                                )
                            },
                            content = { Text(text = stringResource(R.string.import_str)) },
                            supportingContent = {
                                Text(text = stringResource(R.string.import_desc))
                            },
                        )

                        Row(
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            Button(
                                onClick = { onAction(SettingsAction.OnImportData) },
                                enabled = state.importState == ImportState.IDLE,
                                modifier = Modifier.weight(1f),
                            ) {
                                when (state.importState) {
                                    IDLE ->
                                        Icon(
                                            painter = painterResource(R.drawable.play),
                                            contentDescription = "Start",
                                        )

                                    IMPORTING ->
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))

                                    IMPORTED ->
                                        Icon(
                                            painter = painterResource(R.drawable.check_circle),
                                            contentDescription = "Done",
                                        )

                                    FAILURE ->
                                        Icon(
                                            painter = painterResource(R.drawable.warning),
                                            contentDescription = "Fail",
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
