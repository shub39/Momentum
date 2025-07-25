package shub39.momentum.core.domain.data_classes

data class MontageConfig(
    val outputPath: String,
    val delaySeconds: Double = 2.0,
    val width: Int = 1920,
    val height: Int = 1080,
    val frameRate: Int = 30,
    val videoCodec: String = "libx264",
    val outputFormat: String = "mp4",
    val fadeTransition: Boolean = false,
    val fadeDuration: Double = 0.5
)
