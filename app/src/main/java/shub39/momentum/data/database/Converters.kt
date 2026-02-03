package shub39.momentum.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import shub39.momentum.domain.data_classes.AlarmData
import shub39.momentum.domain.data_classes.FaceData

object Converters {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

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