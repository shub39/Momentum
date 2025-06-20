package shub39.momentum.core.domain.data_classes

import kotlinx.serialization.Serializable

// a lapse
@Serializable
data class Project(
    val id: Long = 0,
    val index: Int,
    val title: String,
    val description: String,
    val startDate: Long,
    val lastUpdatedDate: Long,
    val days: List<LapseDay>
)