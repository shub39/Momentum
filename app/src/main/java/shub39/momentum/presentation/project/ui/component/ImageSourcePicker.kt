package shub39.momentum.presentation.project.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.presentation.MomentumPreviewWrapper
import shub39.momentum.presentation.shared.MomentumBottomSheet
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.leadingItemShape

@Composable
fun ImageSourcePicker(
    modifier: Modifier = Modifier,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState
) {
    MomentumBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        padding = 16.dp
    ) {
        ImageSourcePickerContent(
            onOpenGallery = {
                onOpenGallery()
                onDismissRequest()
            },
            onOpenCamera = {
                onOpenCamera()
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun ImageSourcePickerContent(
    modifier: Modifier = Modifier,
    onOpenGallery: () -> Unit,
    onOpenCamera: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        ListItem(
            modifier = Modifier
                .clip(leadingItemShape())
                .clickable { onOpenGallery() },
            headlineContent = {
                Text(text = stringResource(R.string.select_from_gallery))
            },
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.image),
                    contentDescription = null
                )
            },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.arrow_forward),
                    contentDescription = null
                )
            }
        )
        ListItem(
            modifier = Modifier
                .clip(endItemShape())
                .clickable { onOpenCamera() },
            headlineContent = { Text(text = stringResource(R.string.open_camera)) },
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.camera),
                    contentDescription = null
                )
            },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.arrow_forward),
                    contentDescription = null
                )
            }
        )
    }
}

@PreviewWrapper(MomentumPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    ImageSourcePickerContent(
        onOpenGallery = {},
        onOpenCamera = {}
    )
}