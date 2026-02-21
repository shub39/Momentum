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
package shub39.momentum.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import shub39.momentum.core.data_classes.AlarmData
import shub39.momentum.core.data_classes.FaceData

object Converters {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromAlarm(alarm: AlarmData?): String? {
        return alarm?.let { json.encodeToString(AlarmData.serializer(), it) }
    }

    @TypeConverter
    fun toAlarm(data: String?): AlarmData? {
        return data?.let { json.decodeFromString(AlarmData.serializer(), it) }
    }

    @TypeConverter
    fun toFaceData(data: String?): FaceData? {
        return data?.let { json.decodeFromString(FaceData.serializer(), it) }
    }

    @TypeConverter
    fun fromFaceData(faceData: FaceData?): String? {
        return faceData?.let { json.encodeToString(FaceData.serializer(), it) }
    }
}
