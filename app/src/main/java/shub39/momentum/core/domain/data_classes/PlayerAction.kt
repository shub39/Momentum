package shub39.momentum.core.domain.data_classes

import shub39.momentum.core.domain.enums.VideoAction

data class PlayerAction(
    val action: VideoAction,
    val data: Any? = null
)
