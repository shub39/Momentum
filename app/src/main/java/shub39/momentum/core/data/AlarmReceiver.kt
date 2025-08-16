package shub39.momentum.core.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import shub39.momentum.core.domain.interfaces.AlarmScheduler
import shub39.momentum.core.domain.interfaces.ProjectRepository
import shub39.momentum.core.presentation.reminderNotification
import java.time.LocalDate

@Single
class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val TAG = "AlarmReceiver"
    }

    val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action == AlarmSchedulerImpl.ACTION) {
            receiverScope.launch {
                val projectRepo = get<ProjectRepository>()
                val scheduler = get<AlarmScheduler>()

                val projectId = intent.getLongExtra("project_id", -1)
                if (projectId < 0L) return@launch

                val project = projectRepo.getProjectById(projectId) ?: return@launch
                val lastDay = projectRepo.getLastCompletedDay(projectId) ?: return@launch

                if (LocalDate.ofEpochDay(lastDay.date) == LocalDate.now()) {
                    Log.d(TAG, "Already done")
                } else {
                    reminderNotification(context, project)
                }

                scheduler.cancel(project)
                scheduler.schedule(project)
            }
        }
    }
}