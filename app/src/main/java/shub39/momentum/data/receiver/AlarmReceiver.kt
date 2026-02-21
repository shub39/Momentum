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
package shub39.momentum.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import shub39.momentum.core.interfaces.AlarmScheduler
import shub39.momentum.core.interfaces.ProjectRepository
import shub39.momentum.data.AlarmSchedulerImpl
import shub39.momentum.data.reminderNotification

@Single
class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()

        Log.d(TAG, "Received Intent")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (intent != null && intent.action == AlarmSchedulerImpl.Companion.ACTION) {
                    Log.d(TAG, "Processing Notification")

                    val projectRepo = get<ProjectRepository>()
                    val scheduler = get<AlarmScheduler>()

                    val projectId = intent.getLongExtra("project_id", -1)
                    if (projectId < 0L) return@launch

                    val project = projectRepo.getProjectById(projectId) ?: return@launch
                    val lastDay = projectRepo.getLastCompletedDay(projectId)

                    if (lastDay == null || LocalDate.ofEpochDay(lastDay.date) != LocalDate.now()) {
                        reminderNotification(context, project)
                    } else {
                        Log.d(TAG, "Already done")
                    }

                    scheduler.schedule(project)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing intent", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
