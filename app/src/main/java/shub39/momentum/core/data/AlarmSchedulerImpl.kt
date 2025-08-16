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
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

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

        val time = LocalTime.ofSecondOfDay(project.alarm.time)
        val days = project.alarm.days.map { DayOfWeek.valueOf(it) }

        for (day in days) {
            val triggerAtMillis = calculateNextTrigger(day, time)

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION
                putExtra("project_id", project.id)
                putExtra("day_of_week", day.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (project.id.toInt() * 10 + day.value),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )

            Log.d(TAG, "Scheduled alarm for ${day.name} at $time (epoch=$triggerAtMillis)")
        }
    }

    override fun cancel(project: Project) {
        if (project.alarm == null) return

        val days = project.alarm.days.map { DayOfWeek.valueOf(it) }

        for (day in days) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION
                putExtra("project_id", project.id)
                putExtra("day_of_week", day.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (project.id.toInt() * 10 + day.value),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "Cancelled alarm for project=${project.id} day=${day.name}")
        }
    }

    override fun cancelAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmManager.cancelAll()
        }
    }

    private fun calculateNextTrigger(dayOfWeek: DayOfWeek, time: LocalTime): Long {
        val now = LocalDateTime.now()
        var candidate =
            now.withHour(time.hour).withMinute(time.minute).withSecond(time.second).withNano(0)
                .with(TemporalAdjusters.nextOrSame(dayOfWeek))

        if (candidate.isBefore(now)) {
            candidate = candidate.with(TemporalAdjusters.next(dayOfWeek))
        }

        return candidate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}