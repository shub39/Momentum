package shub39.momentum.presentation.project.ui.sections

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import shub39.momentum.R
import shub39.momentum.domain.data_classes.Day
import shub39.momentum.domain.data_classes.Theme
import shub39.momentum.domain.enums.AppTheme
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.project.ProjectState
import shub39.momentum.presentation.shared.MomentumTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayInfo(
    modifier: Modifier = Modifier,
    selectedDate: Long,
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val day = state.days.find { it.date == selectedDate }
    var imageFile: PlatformFile? by remember {
        mutableStateOf(day?.let { PlatformFile(it.image.toUri()) })
    }
    val imagePicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { image ->
        if (image != null) imageFile = image
    }

    DayInfoContent(
        modifier = modifier,
        day = day,
        imageFile = imageFile,
        onLaunchImagePicker = { imagePicker.launch() },
        selectedDate = selectedDate,
        state = state,
        onAction = onAction,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DayInfoContent(
    modifier: Modifier = Modifier,
    day: Day?,
    imageFile: PlatformFile?,
    onLaunchImagePicker: () -> Unit,
    selectedDate: Long,
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(day?.isFavorite ?: false) }
    var comment by remember { mutableStateOf(day?.comment ?: "") }

    Scaffold(
        modifier = modifier
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = LocalDate.ofEpochDay(selectedDate).format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                ),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    )
                    .clip(MaterialTheme.shapes.large)
                    .wrapContentSize()
            ) {
                if (imageFile != null) {
                    CoilImage(
                        imageModel = { imageFile.path.toUri() },
                        loading = { LoadingIndicator() },
                        failure = {
                            Column(
                                modifier = Modifier
                                    .size(300.dp)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.warning),
                                    contentDescription = "Placeholder",
                                    modifier = Modifier.size(100.dp)
                                )

                                Text(
                                    text = stringResource(R.string.select_another_image),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        previewPlaceholder = painterResource(R.drawable.ic_launcher_foreground),
                        modifier = Modifier.heightIn(max = 500.dp),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Fit
                        )
                    )

                    IconToggleButton(
                        checked = isFavorite,
                        onCheckedChange = { isFavorite = it },
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isFavorite) {
                                    R.drawable.favorite
                                } else {
                                    R.drawable.favorite_border
                                }
                            ),
                            contentDescription = "Set Favorite"
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = onLaunchImagePicker,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = "Pick another image"
                        )
                    }

                } else {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.add_photo),
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(200.dp)
                        )

                        TextButton(
                            onClick = onLaunchImagePicker,
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text(text = stringResource(R.string.select_image))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                enabled = imageFile != null,
                singleLine = true,
                isError = comment.length > 100,
                label = {
                    Text(
                        text = if (comment.length > 50) {
                            stringResource(R.string.too_long)
                        } else {
                            stringResource(R.string.add_comment)
                        }
                    )
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (day == null) {
                    Button(
                        onClick = {
                            onAction(
                                ProjectAction.OnUpsertDay(
                                    Day(
                                        projectId = state.project?.id!!,
                                        image = imageFile!!.path.toUri().toString(),
                                        comment = comment,
                                        date = selectedDate,
                                        isFavorite = isFavorite
                                    )
                                )
                            )

                            onNavigateBack()
                        },
                        enabled = imageFile != null,
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.add_day)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            onAction(ProjectAction.OnDeleteDay(day))
                            onNavigateBack()
                        }
                    ) {
                        Text(text = stringResource(R.string.delete_day))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            onAction(
                                ProjectAction.OnUpsertDay(
                                    day.copy(
                                        image = imageFile!!.path.toUri().toString(),
                                        comment = comment,
                                        isFavorite = isFavorite
                                    )
                                )
                            )
                            onNavigateBack()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = (day.image.toUri() != imageFile?.path?.toUri() || day.isFavorite != isFavorite || day.comment != comment) && comment.length <= 50
                    ) {
                        Text(
                            text = stringResource(R.string.update_day)
                        )
                    }
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
            appTheme = AppTheme.DARK
        )
    ) {
        DayInfoContent(
            modifier = Modifier,
            day = null,
            imageFile = null,
            onLaunchImagePicker = { },
            selectedDate = 0,
            state = ProjectState(),
            onAction = { },
            onNavigateBack = { }
        )
    }
}