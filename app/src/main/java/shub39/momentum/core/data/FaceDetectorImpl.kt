package shub39.momentum.core.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import shub39.momentum.core.domain.data_classes.FaceData
import shub39.momentum.core.domain.interfaces.FaceDetector

@Single(binds = [FaceDetector::class])
class FaceDetectorImpl(
    context: Context
) : FaceDetector {
    private val detectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .build()
    private val faceDetector = FaceDetection.getClient(detectorOptions)
    private val contentResolver = context.contentResolver

    override suspend fun getFaceDataFromUri(uri: Uri): FaceData {
        val imageStream = contentResolver.openInputStream(uri)
        val imageBitmap = BitmapFactory.decodeStream(imageStream)
        if (imageBitmap == null) return FaceData() // null means invalid image or some kind of error

        val inputImage = InputImage.fromBitmap(imageBitmap, 0)
        val faces = withContext(Dispatchers.Default) {
            Tasks.await(faceDetector.process(inputImage))
        }

        if (faces.isEmpty()) return FaceData() // meaning no faces :'(

        val face = faces.maxByOrNull {
            it.boundingBox.width() * it.boundingBox.height()
        }!!

        return FaceData(
            bottom = face.boundingBox.bottom,
            left = face.boundingBox.left,
            right = face.boundingBox.right,
            top = face.boundingBox.top,
            headAngle = face.headEulerAngleZ
        )
    }
}