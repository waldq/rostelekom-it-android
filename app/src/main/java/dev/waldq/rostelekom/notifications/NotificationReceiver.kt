package dev.waldq.rostelekom.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.waldq.rostelekom.R

class NotificationReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {

        val channelId = "reminders"

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // Создаём канал уведомлений
        val channel = NotificationChannel(
            channelId,
            "Напоминания",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)

        // Создаём уведомление
        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Напоминание")
            .setContentText("Не забудьте проверить приложение!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(1, notification)
    }
}