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

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import shub39.momentum.R
import shub39.momentum.presentation.MomentumPreviewWrapper
import shub39.momentum.presentation.shared.MomentumBottomSheet
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.leadingItemShape

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageSourcePicker(
    modifier: Modifier = Modifier,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val cameraPermissionState =
        rememberPermissionState(Manifest.permission.CAMERA) { granted ->
            if (granted) {
                onOpenCamera()
            }
            onDismissRequest()
        }

    MomentumBottomSheet(modifier = modifier, onDismissRequest = onDismissRequest, padding = 16.dp) {
        ImageSourcePickerContent(
            onOpenGallery = {
                onOpenGallery()
                onDismissRequest()
            },
            onOpenCamera = {
                if (cameraPermissionState.status.isGranted) {
                    onOpenCamera()
                    onDismissRequest()
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            },
        )
    }
}

@Composable
private fun ImageSourcePickerContent(
    modifier: Modifier = Modifier,
    onOpenGallery: () -> Unit,
    onOpenCamera: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        ListItem(
            modifier = Modifier.clip(leadingItemShape()).clickable { onOpenGallery() },
            content = { Text(text = stringResource(R.string.select_from_gallery)) },
            leadingContent = {
                Icon(painter = painterResource(R.drawable.image), contentDescription = null)
            },
            trailingContent = {
                Icon(painter = painterResource(R.drawable.arrow_forward), contentDescription = null)
            },
        )
        ListItem(
            modifier = Modifier.clip(endItemShape()).clickable { onOpenCamera() },
            content = { Text(text = stringResource(R.string.open_camera)) },
            leadingContent = {
                Icon(painter = painterResource(R.drawable.camera), contentDescription = null)
            },
            trailingContent = {
                Icon(painter = painterResource(R.drawable.arrow_forward), contentDescription = null)
            },
        )
    }
}

@PreviewWrapper(MomentumPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    ImageSourcePickerContent(onOpenGallery = {}, onOpenCamera = {})
}
