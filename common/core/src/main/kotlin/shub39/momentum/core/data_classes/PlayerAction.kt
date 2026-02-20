package shub39.momentum.core.data_classes

import shub39.momentum.core.enums.VideoAction

data class PlayerAction(
    val action: VideoAction,
    val data: Any? = null
)
