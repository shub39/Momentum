package shub39.momentum.domain.interfaces

import android.net.Uri
import shub39.momentum.domain.data_classes.FaceData

interface FaceDetector {
    suspend fun getFaceDataFromUri(uri: Uri): FaceData
}