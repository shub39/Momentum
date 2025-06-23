package shub39.momentum.core.domain.data_classes

data class Project(
    val id: Long = 0,
    val index: Int,
    val title: String,
    val description: String,
    val startDate: Long,
    val lastUpdatedDate: Long,
    val goalDate: Long
)