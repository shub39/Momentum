package shub39.momentum.core.domain.interfaces

import android.net.Uri
import shub39.momentum.core.domain.data_classes.FaceData

interface FaceDetector {
    suspend fun getFaceDataFromUri(uri: Uri): FaceData
}