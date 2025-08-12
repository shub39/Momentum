package shub39.momentum.core.domain.enums

enum class VideoQuality {
    SMALL,
    HD;

    companion object {
        fun VideoQuality.toDimensions(): Pair<Int, Int> {
            return when (this) {
                SMALL -> Pair(768, 1024)
                HD -> Pair(1080, 1440)
            }
        }
    }
}