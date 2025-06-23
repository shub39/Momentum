package shub39.momentum.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
sealed interface ProjectDao {
    @Query("SELECT * FROM projects_table")
    fun getProjects(): Flow<List<ProjectEntity>>

    @Upsert
    suspend fun upsertProject(projectEntity: ProjectEntity)

    @Delete
    suspend fun deleteProject(projectEntity: ProjectEntity)
}