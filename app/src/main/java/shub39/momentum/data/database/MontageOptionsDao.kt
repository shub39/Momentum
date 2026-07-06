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

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
sealed interface MontageOptionsDao {
    @Query("SELECT * FROM options_table")
    fun getMontageOptions(): Flow<List<MontageOptionsEntity>>

    @Upsert suspend fun upsertMontageOption(montageOptions: MontageOptionsEntity)

    @Delete suspend fun deleteMontageOption(montageOptions: MontageOptionsEntity)

    @Query("SELECT * FROM options_table WHERE projectId=:projectId")
    suspend fun getMontageOptionByProjectId(projectId: Long): MontageOptionsEntity?
}
