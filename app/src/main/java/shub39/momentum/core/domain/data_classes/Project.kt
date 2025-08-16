package shub39.momentum.core.domain.data_classes

data class Project(
    val id: Long = 0,
    val title: String,
    val description: String,
    val alarm: AlarmData? = null
)