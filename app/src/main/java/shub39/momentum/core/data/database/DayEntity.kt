package shub39.momentum.core.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import shub39.momentum.core.domain.data_classes.FaceData

@Serializable
@Entity(
    tableName = "days_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class DayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val image: String,
    val comment: String?,
    val date: Long,
    val isFavorite: Boolean,
    @ColumnInfo(name = "faceData", defaultValue = "NULL") val faceData: FaceData? = null
)