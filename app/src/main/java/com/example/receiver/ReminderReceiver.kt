package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "REXER Hub Smart Reminder"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Check your daily financial targets & schedule!"
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "Smart Reminder"
        val notificationId = intent.getIntExtra("EXTRA_ID", (System.currentTimeMillis() % 10000).toInt())

        NotificationHelper.showNotification(
            context = context,
            notificationId = notificationId,
            title = title,
            message = message,
            category = category
        )
    }
}
