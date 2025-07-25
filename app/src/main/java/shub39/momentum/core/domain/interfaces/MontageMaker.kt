package shub39.momentum.core.domain.interfaces

import shub39.momentum.core.domain.data_classes.MontageConfig

interface MontageMaker {
    suspend fun createMontage(
        imagePaths: List<String>,
        config: MontageConfig,
        onProgress: ((Float) -> Unit)? = null
    ): MontageResult

    companion object {
        val supportedExtensions = setOf(
            "jpg", "jpeg", "png", "bmp", "tiff", "webp"
        )
    }
}