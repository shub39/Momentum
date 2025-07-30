package shub39.momentum.core.domain.data_classes

data class MontageConfig(
    val columns: Int = 0,
    val rows: Int = 0,
    val imageWidth: Int = 320,
    val imageHeight: Int = 240,
    val padding: Int = 10,
    val backgroundColor: String = "black"
)