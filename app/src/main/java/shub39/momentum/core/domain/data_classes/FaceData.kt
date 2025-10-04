package shub39.momentum.core.domain.data_classes

import android.graphics.Rect
import kotlinx.serialization.Serializable

@Serializable
data class FaceData(
    val bottom: Int = -1,
    val left: Int = -1,
    val right: Int = -1,
    val top: Int = -1,
    val headAngle: Float = -1f
)

fun FaceData?.isValid(): Boolean {
    return this != null && this != FaceData()
}

fun FaceData.toRect(): Rect {
    return Rect(left, top, right, bottom)
}