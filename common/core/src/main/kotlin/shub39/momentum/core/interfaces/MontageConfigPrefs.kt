/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.momentum.core.interfaces

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import shub39.momentum.core.enums.DateStyle
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.VideoQuality

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

    fun getCensorPref(): Flow<Boolean>

    suspend fun setCensorPref(pref: Boolean)
}
