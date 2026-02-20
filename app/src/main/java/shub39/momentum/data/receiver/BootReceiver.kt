package shub39.momentum.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import shub39.momentum.core.interfaces.AlarmScheduler
import shub39.momentum.data.database.ProjectDao
import shub39.momentum.data.toProject

@Single
class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = get<AlarmScheduler>()
            val repo = get<ProjectDao>()

            receiverScope.launch {
                repo.getProjects().first().forEach {
                    scheduler.schedule(it.toProject())
                }
            }
        }
    }
}