package shub39.momentum.core.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import shub39.momentum.core.domain.data_classes.AlarmData

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
}