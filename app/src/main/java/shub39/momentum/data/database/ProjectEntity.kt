package shub39.momentum.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import shub39.momentum.domain.data_classes.AlarmData

@Serializable
@Entity(tableName = "projects_table")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val alarm: AlarmData? = null
)