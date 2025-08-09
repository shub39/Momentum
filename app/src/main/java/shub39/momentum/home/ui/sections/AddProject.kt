package shub39.momentum.home.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Theme
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.home.HomeAction

@Composable
fun AddProject(
    onAction: (HomeAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    var newProjectTitle by remember { mutableStateOf("") }
    var newProjectDescription by remember { mutableStateOf("") }

    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new project",
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = stringResource(R.string.start_new_project),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = newProjectTitle,
                    onValueChange = { newProjectTitle = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = newProjectTitle.length >= 20,
                    placeholder = { Text(text = stringResource(R.string.title)) },
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = newProjectDescription,
                    onValueChange = { newProjectDescription = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = newProjectDescription.length >= 100,
                    placeholder = { Text(text = stringResource(R.string.description)) },
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                )

                Button(
                    onClick = {
                        onAction(
                            HomeAction.OnAddProject(
                                title = newProjectTitle,
                                description = newProjectDescription
                            )
                        )
                        onNavigateBack()
                    },
                    enabled = newProjectTitle.length <= 20 && newProjectDescription.length <= 100 && newProjectTitle.isNotBlank(),
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.add_new_project))
                }

                TextButton(
                    onClick = onNavigateBack
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    MomentumTheme(
        theme = Theme(
            seedColor = Color.Yellow,
            appTheme = AppTheme.DARK,
            font = Fonts.FIGTREE,
            paletteStyle = PaletteStyle.Fidelity
        )
    ) {
        AddProject(
            onAction = {},
            onNavigateBack = {}
        )
    }
}