package shub39.momentum.core.interfaces

import android.net.Uri
import shub39.momentum.core.data_classes.FaceData

interface FaceDetector {
    suspend fun getFaceDataFromUri(uri: Uri): FaceData
}