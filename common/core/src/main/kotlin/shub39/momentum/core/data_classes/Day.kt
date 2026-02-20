package shub39.momentum.core.data_classes

// individual Days
// FaceData(-1, -1, -1, ...) means no face
data class Day(
    val id: Long = 0,
    val projectId: Long,
    val image: String, // Uri.toString()
    val comment: String?,
    val date: Long,
    val isFavorite: Boolean,
    val faceData: FaceData? = null
)
