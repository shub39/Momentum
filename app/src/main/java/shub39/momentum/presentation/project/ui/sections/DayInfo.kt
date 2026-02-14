package shub39.momentum.presentation.project.ui.sections

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.delay
import shub39.momentum.R
import shub39.momentum.domain.data_classes.Day
import shub39.momentum.domain.data_classes.Theme
import shub39.momentum.domain.data_classes.isValid
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
    var imageFile: PlatformFile? by remember(day) {
        mutableStateOf(day?.let { PlatformFile(it.image.toUri()) })
    }
    var isFavorite by remember(day) { mutableStateOf(day?.isFavorite ?: false) }
    var comment by remember(day) { mutableStateOf(day?.comment ?: "") }

    val imagePicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { image ->
        if (image != null) {
            imageFile = image

            onAction(
                ProjectAction.OnUpsertDay(
                    day = day?.copy(
                        image = image.path.toUri().toString()
                    ) ?: Day(
                        projectId = state.project?.id!!,
                        image = image.path.toUri().toString(),
                        comment = comment,
                        date = selectedDate,
                        isFavorite = isFavorite
                    ),
                    isNewImage = true
                )
            )
        }
    }

    DayInfoContent(
        modifier = modifier,
        day = day,
        imageFile = imageFile,
        onLaunchImagePicker = { imagePicker.launch() },
        selectedDate = selectedDate,
        onAction = onAction,
        isFavorite = isFavorite,
        comment = comment,
        onNavigateBack = onNavigateBack,
        onUpdateComment = { comment = it },
        onUpdateFavorite = {
            isFavorite = it

            day?.let { day ->
                onAction(ProjectAction.OnUpsertDay(day.copy(isFavorite = it)))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DayInfoContent(
    modifier: Modifier = Modifier,
    day: Day?,
    imageFile: PlatformFile?,
    isFavorite: Boolean,
    comment: String,
    onUpdateComment: (String) -> Unit,
    onUpdateFavorite: (Boolean) -> Unit,
    onLaunchImagePicker: () -> Unit,
    selectedDate: Long,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showCommentModal by remember { mutableStateOf(false) }
    var highlightFace by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = LocalDate.ofEpochDay(selectedDate).format(
                            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                        ),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            onLaunchImagePicker()
                            highlightFace = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.image),
                            contentDescription = null
                        )
                    }
                }
            ) {
                IconButton(
                    onClick = {
                        day?.let { onAction(ProjectAction.OnDeleteDay(it)) }
                        onNavigateBack()
                    },
                    enabled = day != null
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = null
                    )
                }

                IconToggleButton(
                    checked = highlightFace,
                    onCheckedChange = { highlightFace = !highlightFace },
                    enabled = day?.faceData.isValid()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.detect_face),
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = { showCommentModal = true },
                    enabled = day != null
                ) {
                    Icon(
                        painter = painterResource(R.drawable.comment),
                        contentDescription = null
                    )
                }

                IconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = { onUpdateFavorite(it) },
                    enabled = day != null
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
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageFile != null) {
                val context = LocalContext.current
                val zoomProgress by animateFloatAsState(
                    targetValue = if (highlightFace && day?.faceData.isValid()) 1f else 0f,
                    animationSpec = tween(durationMillis = 500),
                    label = "faceZoom"
                )

                // original image
                val originalBitmap = remember(imageFile) {
                    context.contentResolver.openInputStream(imageFile.path.toUri())?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }

                // cropped image around the face
                val croppedFaceBitmap = remember(imageFile, day?.faceData) {
                    if (originalBitmap != null && day?.faceData.isValid()) {
                        val faceData = day?.faceData!!

                        val faceWidth = faceData.right - faceData.left
                        val faceHeight = faceData.bottom - faceData.top
                        val padding = (maxOf(faceWidth, faceHeight) * 0.5f).toInt()

                        val cropLeft = (faceData.left - padding).coerceAtLeast(0)
                        val cropTop = (faceData.top - padding).coerceAtLeast(0)
                        val cropRight =
                            (faceData.right + padding).coerceAtMost(originalBitmap.width)
                        val cropBottom =
                            (faceData.bottom + padding).coerceAtMost(originalBitmap.height)

                        val cropWidth = cropRight - cropLeft
                        val cropHeight = cropBottom - cropTop

                        Bitmap.createBitmap(
                            originalBitmap,
                            cropLeft,
                            cropTop,
                            cropWidth,
                            cropHeight
                        )
                    } else {
                        null
                    }
                }

                CoilImage(
                    imageModel = { imageFile.path.toUri() },
                    loading = { LoadingIndicator() },
                    success = { _, painter ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.large),
                            contentAlignment = Alignment.Center
                        ) {
                            // Show full image with fade out
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        alpha = 1f - zoomProgress
                                    },
                                contentScale = ContentScale.Fit
                            )

                            // Show cropped face with fade in
                            if (croppedFaceBitmap != null && day?.faceData.isValid()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        bitmap = croppedFaceBitmap.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .graphicsLayer {
                                                alpha = zoomProgress
                                            },
                                        contentScale = ContentScale.Fit
                                    )

                                    // Draw bounding box
                                    if (zoomProgress > 0.01f) {
                                        val faceData = day?.faceData!!

                                        Canvas(modifier = Modifier.matchParentSize()) {
                                            // Calculate where the face is in the cropped bitmap
                                            val faceWidth = faceData.right - faceData.left
                                            val faceHeight = faceData.bottom - faceData.top
                                            val padding =
                                                (maxOf(faceWidth, faceHeight) * 0.5f).toInt()

                                            val cropLeft =
                                                (faceData.left - padding).coerceAtLeast(0)
                                            val cropTop = (faceData.top - padding).coerceAtLeast(0)

                                            val faceLeftInCrop = faceData.left - cropLeft
                                            val faceTopInCrop = faceData.top - cropTop
                                            val faceRightInCrop = faceData.right - cropLeft
                                            val faceBottomInCrop = faceData.bottom - cropTop

                                            // Calculate scale from bitmap to canvas
                                            val bitmapWidth = croppedFaceBitmap.width.toFloat()
                                            val bitmapHeight = croppedFaceBitmap.height.toFloat()

                                            val scaleX = size.width / bitmapWidth
                                            val scaleY = size.height / bitmapHeight
                                            val scale = minOf(scaleX, scaleY)

                                            val displayedWidth = bitmapWidth * scale
                                            val displayedHeight = bitmapHeight * scale
                                            val offsetX = (size.width - displayedWidth) / 2f
                                            val offsetY = (size.height - displayedHeight) / 2f

                                            // Convert to canvas coordinates
                                            val canvasFaceLeft = faceLeftInCrop * scale + offsetX
                                            val canvasFaceTop = faceTopInCrop * scale + offsetY
                                            val canvasFaceRight = faceRightInCrop * scale + offsetX
                                            val canvasFaceBottom =
                                                faceBottomInCrop * scale + offsetY

                                            val boxWidth = canvasFaceRight - canvasFaceLeft
                                            val boxHeight = canvasFaceBottom - canvasFaceTop
                                            val centerX = canvasFaceLeft + boxWidth / 2f
                                            val centerY = canvasFaceTop + boxHeight / 2f

                                            rotate(
                                                degrees = faceData.headAngle,
                                                pivot = Offset(centerX, centerY)
                                            ) {
                                                drawRect(
                                                    color = Color.Green.copy(alpha = zoomProgress),
                                                    topLeft = Offset(canvasFaceLeft, canvasFaceTop),
                                                    size = Size(boxWidth, boxHeight),
                                                    style = Stroke(width = 4.dp.toPx())
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
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
                    modifier = Modifier.fillMaxWidth(),
                    imageOptions = ImageOptions(contentScale = ContentScale.Fit)
                )
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

            if (showCommentModal) {
                ModalBottomSheet(
                    onDismissRequest = { showCommentModal = false }
                ) {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val focusRequester = remember { FocusRequester() }

                    LaunchedEffect(Unit) {
                        delay(200)
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { if (it.length <= 50) onUpdateComment(it) },
                        enabled = imageFile != null,
                        singleLine = true,
                        isError = comment.length > 100,
                        label = {
                            Text(
                                text = stringResource(R.string.add_comment)
                            )
                        },
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .imePadding()
                            .focusRequester(focusRequester)
                    )

                    Button(
                        onClick = {
                            showCommentModal = false

                            day?.let { day ->
                                onAction(ProjectAction.OnUpsertDay(day.copy(comment = comment)))
                            }
                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.done))
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
            onAction = { },
            onNavigateBack = { },
            isFavorite = true,
            comment = "",
            onUpdateComment = { },
            onUpdateFavorite = { },
        )
    }
}