package shub39.momentum.domain.data_classes

import shub39.momentum.domain.enums.VideoAction

data class PlayerAction(
    val action: VideoAction,
    val data: Any? = null
)
