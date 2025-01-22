package com.example.eventapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("notification_title") ?: "Default Title"
        val message = intent.getStringExtra("notification_message") ?: "Default Message"

        // Bildirim izninin olup olmadığını kontrol ediyoruz
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Bildirim oluştur ve gönder
            val builder = NotificationCompat.Builder(context, "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1, builder.build())
        } else {
            println("Notification permission not granted!")
        }
    }
}
