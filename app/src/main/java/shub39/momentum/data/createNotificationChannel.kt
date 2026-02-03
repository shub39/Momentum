package shub39.momentum.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context


fun createNotificationChannel(context: Context) {
    val name = "Momentum"
    val descriptionText = "Reminders for montages"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("1", name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    notificationManager.createNotificationChannel(channel)
}