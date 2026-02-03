package shub39.momentum.domain.data_classes

import kotlinx.serialization.Serializable

@Serializable
data class AlarmData(
    val time: Long,
    val days: List<String>
)
