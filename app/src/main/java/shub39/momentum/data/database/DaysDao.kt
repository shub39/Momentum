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
