package shub39.momentum.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import shub39.momentum.R
import shub39.momentum.domain.data_classes.Project

fun reminderNotification(
    context: Context,
    project: Project
) {
    val text = if (project.description.isBlank()) {
        project.title
    } else {
        "${project.title} : ${project.description}"
    }

    val builder = NotificationCompat
        .Builder(context, "1")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(context.getString(R.string.take_a_photo))
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notify(project.id.hashCode(), builder.build())
        }
    }
}