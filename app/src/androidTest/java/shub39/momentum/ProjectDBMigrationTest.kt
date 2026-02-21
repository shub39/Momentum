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
package shub39.momentum

import android.content.ContentValues
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shub39.momentum.data.database.ProjectDatabase

private const val DB_NAME = "projects_test.db"

@RunWith(AndroidJUnit4::class)
class ProjectDBMigrationTest {
    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            ProjectDatabase::class.java,
            listOf(),
            FrameworkSQLiteOpenHelperFactory(),
        )

    @Test
    fun migration1to2_containsCorrectData() {
        helper.createDatabase(DB_NAME, 1).apply {
            // Insert a project into the old version 1 database
            val projectValues =
                ContentValues().apply {
                    put("id", 1)
                    put("title", "First Project")
                    put("description", "A description for the first project.")
                    put("alarm", "10:00")
                }
            insert("projects_table", 0, projectValues)

            // Insert a 'day' for that project
            val dayValues =
                ContentValues().apply {
                    put("projectId", 1)
                    put("image", "/path/to/image.jpg")
                    put("comment", "A comment for the day.")
                    put("date", 1672531200L) // Example date (Jan 1, 2023)
                    put("isFavorite", 1)
                }
            insert("days_table", 0, dayValues)

            close()
        }

        // Run the migration to version 2 and validate the schema.
        val db = helper.runMigrationsAndValidate(DB_NAME, 2, true)

        // Validate the data after migration
        db.query("SELECT * FROM projects_table WHERE id = 1").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow("title")))
                .isEqualTo("First Project")
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")))
                .isEqualTo("A description for the first project.")
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow("alarm"))).isEqualTo("10:00")
        }

        db.query("SELECT * FROM days_table WHERE projectId = 1").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow("image")))
                .isEqualTo("/path/to/image.jpg")
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow("comment")))
                .isEqualTo("A comment for the day.")
            assertThat(cursor.getLong(cursor.getColumnIndexOrThrow("date"))).isEqualTo(1672531200L)
            assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite"))).isEqualTo(1)
        }
    }

    @Test
    fun testAllMigrations() {
        // Create schema 1 and close it.
        helper.createDatabase(DB_NAME, 1).apply { close() }

        // Open the database with the latest version to run all migrations.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ProjectDatabase::class.java,
            DB_NAME,
        )
            .build()
            .apply { openHelper.writableDatabase.close() }
    }
}
