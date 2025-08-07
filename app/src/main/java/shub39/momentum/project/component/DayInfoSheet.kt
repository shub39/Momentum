package shub39.momentum.project.component

import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.skydoves.landscapist.coil3.CoilImage
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.uri
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.project.ProjectAction
import shub39.momentum.project.ProjectState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayInfoSheet(
    selectedDate: Long,
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    modifier: Modifier = Modifier
) {
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
        onDismissRequest = { onAction(ProjectAction.OnUpdateSelectedDay(null)) },
        modifier = modifier
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
                                projectId = state.project?.id!!,
                                image = imageFile!!.uri.toString(),
                                comment = null,
                                date = selectedDate!!,
                                isFavorite = false
                            )
                        )
                    )

                    onAction(ProjectAction.OnUpdateSelectedDay(null))
                },
                enabled = imageFile != null
            ) { Text("Add Day") }
        } else {
            Button(
                onClick = {
                    onAction(ProjectAction.OnDeleteDay(day))
                    onAction(ProjectAction.OnUpdateSelectedDay(null))
                }
            ) {
                Text("Delete Day")
            }
        }
    }
}