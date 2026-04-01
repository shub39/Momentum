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
package shub39.momentum.presentation.home.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle
import shub39.momentum.presentation.shared.MomentumBottomSheet
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.flexFontEmphasis

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectUpsertSheet(
    project: Project,
    onUpsertProject: (Project) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    edit: Boolean = false,
) {
    var newProject by remember { mutableStateOf(project) }

    MomentumBottomSheet(
        modifier = modifier.imePadding(),
        padding = 0.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            Box(
                modifier =
                    Modifier.size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialShapes.Pill.toShape(),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(if (edit) R.drawable.edit else R.drawable.add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Text(
                text =
                    stringResource(if (edit) R.string.edit_project else R.string.start_new_project),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = flexFontEmphasis()),
            )

            OutlinedTextField(
                value = newProject.title,
                onValueChange = { newProject = newProject.copy(title = it) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = newProject.title.length >= 20,
                placeholder = { Text(text = stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = newProject.description,
                onValueChange = { newProject = newProject.copy(description = it) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = newProject.description.length >= 100,
                placeholder = { Text(text = stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    onUpsertProject(newProject)
                    onDismissRequest()
                },
                enabled =
                    newProject.title.length <= 20 &&
                        newProject.description.length <= 100 &&
                        newProject.title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.add_new_project))
            }

            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    MomentumTheme(
        theme =
            Theme(
                seedColor = Color.Yellow,
                appTheme = AppTheme.DARK,
                font = Fonts.FIGTREE,
                paletteStyle = PaletteStyle.FIDELITY,
            )
    ) {
        ProjectUpsertSheet(
            project = Project(title = "", description = ""),
            onUpsertProject = {},
            onDismissRequest = {},
        )
    }
}
