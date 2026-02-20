package shub39.momentum.core.data_classes

data class Project(
    val id: Long = 0,
    val title: String,
    val description: String,
    val alarm: AlarmData? = null
)