package shub39.momentum.presentation.project.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
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
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayInfoSheet(
    selectedDate: Long,
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val day = state.days.find { it.date == selectedDate }

    var isFavorite by remember { mutableStateOf(day?.isFavorite ?: false) }
    var comment by remember { mutableStateOf(day?.comment ?: "") }
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
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onAction(ProjectAction.OnUpdateSelectedDay(null)) },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp)
                .fillMaxWidth(),
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
                        imageModel = { imageFile!!.path.toUri() },
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
                                    imageVector = Icons.Rounded.Warning,
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
                            imageVector = if (isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Set Favorite"
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = { imagePicker.launch() },
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Pick another image"
                        )
                    }

                } else {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(200.dp)
                        )

                        TextButton(
                            onClick = { imagePicker.launch() },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { event ->
                        if (event.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
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

                            onAction(ProjectAction.OnUpdateSelectedDay(null))
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
                            onAction(ProjectAction.OnUpdateSelectedDay(null))
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
                            onAction(ProjectAction.OnUpdateSelectedDay(null))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun Preview() {
    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        DayInfoSheet(
            selectedDate = 1,
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
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            )
        )
    }
}