package shub39.momentum.core.domain.data_classes

// individual Days
data class Day(
    val id: Long = 0,
    val projectId: Long,
    val image: String, // Uri.toString()
    val comment: String?,
    val date: Long,
    val isFavorite: Boolean
)
