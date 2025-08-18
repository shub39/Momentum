package shub39.momentum.core.domain.interfaces

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import shub39.momentum.core.domain.enums.DateStyle
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.domain.enums.VideoQuality

interface MontageConfigPrefs {
    suspend fun resetPrefs()

    fun getFpiFlow(): Flow<Int>
    suspend fun setFpi(fpi: Int)

    fun getFpsFlow(): Flow<Float>
    suspend fun setFps(fps: Float)

    fun getVideoQualityFlow(): Flow<VideoQuality>
    suspend fun setVideoQuality(quality: VideoQuality)

    fun getBackgroundColorFlow(): Flow<Color>
    suspend fun setBackgroundColor(color: Color)

    fun getWaterMarkFlow(): Flow<Boolean>
    suspend fun setWaterMark(show: Boolean)

    fun getShowDateFlow(): Flow<Boolean>
    suspend fun setShowDate(show: Boolean)

    fun getShowMessageFlow(): Flow<Boolean>
    suspend fun setShowMessage(show: Boolean)

    fun getFontFlow(): Flow<Fonts>
    suspend fun setFont(font: Fonts)

    fun getDateStyleFlow(): Flow<DateStyle>
    suspend fun setDateStyle(style: DateStyle)

    fun getStabilizeFacesFlow(): Flow<Boolean>
    suspend fun setStabilizeFaces(pref: Boolean)
}