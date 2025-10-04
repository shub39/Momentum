package shub39.momentum.core.domain.data_classes

import kotlinx.serialization.Serializable

@Serializable
data class FaceData(
    val faceCenterX: Float,
    val faceCenterY: Float,
    val scale: Float,
    val targetCenterX: Float,
    val targetCenterY: Float,
    val headEulerAngleZ: Float
)
