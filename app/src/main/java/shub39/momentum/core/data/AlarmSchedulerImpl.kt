package shub39.momentum.core.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import org.koin.core.annotation.Single
import shub39.momentum.core.domain.data_classes.Project
import shub39.momentum.core.domain.interfaces.AlarmScheduler
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@Single(binds = [AlarmScheduler::class])
class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {

    companion object {
        private const val TAG = "NotificationAlarmScheduler"
        const val ACTION = "notification_reminder"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(project: Project) {
        if (project.alarm == null) return

        cancel(project)

        val time = LocalTime.ofSecondOfDay(project.alarm.time)
        val now = LocalDateTime.now()
        val today = now.toLocalDate()

        val scheduledDate = if (time.isAfter(now.toLocalTime())) today else today.plusDays(1)

        val nextDateTime = LocalDateTime.of(scheduledDate, time)
            .atZone(ZoneId.systemDefault())

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION
            putExtra("project_id", project.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            project.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextDateTime.toEpochSecond() * 1000,
            pendingIntent
        )

        Log.d(TAG, "Scheduled alarm for ${project.title} at $nextDateTime)")
    }

    override fun cancel(project: Project) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION
            putExtra("project_id", project.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            project.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled alarm for project=${project.id}")
    }

    override fun cancelAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmManager.cancelAll()
        }
    }
}