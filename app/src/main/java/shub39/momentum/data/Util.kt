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
package shub39.momentum.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import shub39.momentum.R
import shub39.momentum.app.MainActivity
import shub39.momentum.core.data_classes.Project

fun reminderNotification(context: Context, project: Project) {
    val text =
        if (project.description.isBlank()) {
            project.title
        } else {
            "${project.title} : ${project.description}"
        }

    val intent =
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val builder =
        NotificationCompat.Builder(context, "1")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.take_a_photo))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        ) {
            notify(project.id.hashCode(), builder.build())
        }
    }
}


fun getPlaceholder(): Painter {
    return BitmapPainter(
        createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            .apply { eraseColor(Color.GREEN) }
            .asImageBitmap()
    )
}


fun createNotificationChannel(context: Context) {
    val name = "Momentum"
    val descriptionText = "Reminders for montages"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("1", name, importance).apply { description = descriptionText }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    notificationManager.createNotificationChannel(channel)
}
