package shub39.momentum.video

import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.domain.data_classes.Project

data class VideoState(
    val project: Project? = null,
    val days: List<Day> = emptyList()
)