package shub39.momentum.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
sealed interface DaysDao {
    @Query("SELECT * FROM days_table WHERE projectId=:id ORDER BY date DESC LIMIT 10")
    suspend fun getLastDaysById(id: Long): List<DayEntity>

    @Query("SELECT * FROM days_table")
    fun getDays(): Flow<List<DayEntity>>

    @Upsert
    suspend fun upsertDay(dayEntity: DayEntity)

    @Delete
    suspend fun deleteDay(dayEntity: DayEntity)
}