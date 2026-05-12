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

import androidx.room3.Room
import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shub39.momentum.data.database.ProjectDatabase

@RunWith(AndroidJUnit4::class)
class ProjectDBMigrationTest {

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val dbFile: File = targetContext.getDatabasePath("projects_test.db")

    @get:Rule
    val helper =
        MigrationTestHelper(
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            databaseClass = ProjectDatabase::class,
            driver = AndroidSQLiteDriver(),
            file = dbFile,
        )

    @After
    fun tearDown() {
        if (dbFile.exists()) dbFile.delete()
    }

    @Test
    fun migration1to2_containsCorrectData() = runBlocking {
        // Ensure the database directory exists
        dbFile.parentFile?.mkdirs()

        helper.createDatabase(1).apply {
            // Insert a project into the old version 1 database
            execSQL(
                "INSERT INTO projects_table (id, title, description, alarm) VALUES (1, 'First Project', 'A description for the first project.', '10:00')"
            )

            // Insert a 'day' for that project
            execSQL(
                "INSERT INTO days_table (projectId, image, comment, date, isFavorite) VALUES (1, '/path/to/image.jpg', 'A comment for the day.', 1672531200, 1)"
            )

            close()
        }

        // Run the migration to version 2 and validate the schema.
        val connection = helper.runMigrationsAndValidate(2)

        // Validate the data after migration
        connection.prepare("SELECT * FROM projects_table WHERE id = 1").apply {
            assertThat(step()).isTrue()
            assertThat(getText(1)).isEqualTo("First Project")
            assertThat(getText(2)).isEqualTo("A description for the first project.")
            assertThat(getText(3)).isEqualTo("10:00")
            close()
        }

        connection.prepare("SELECT * FROM days_table WHERE projectId = 1").apply {
            assertThat(step()).isTrue()
            assertThat(getText(2)).isEqualTo("/path/to/image.jpg")
            assertThat(getText(3)).isEqualTo("A comment for the day.")
            assertThat(getLong(4)).isEqualTo(1672531200L)
            assertThat(getLong(5)).isEqualTo(1L)
            close()
        }

        connection.close()
    }

    @Test
    fun testAllMigrations() = runBlocking {
        // Ensure the database directory exists
        dbFile.parentFile?.mkdirs()

        // Create schema 1 and close it.
        helper.createDatabase(1).close()

        // Open the database with the latest version to run all migrations.
        Room.databaseBuilder(targetContext, ProjectDatabase::class.java, dbFile.absolutePath)
            .setDriver(AndroidSQLiteDriver())
            .build()
            .close()
    }
}
