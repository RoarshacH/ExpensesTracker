package com.example.expesestracker.models

import android.app.Notification
import android.app.NotificationChannel

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context

import android.content.Intent
import android.os.Build

private const val CHANNEL_ID = "Notify_CHANNEL"

class MyNotificationPublisher: BroadcastReceiver() {
   var NOTIFICATION_ID = "notification-id"
   var NOTIFICATION = "notification"

   override fun onReceive(context: Context, intent: Intent) {
      val notificationManager =
         context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val notification: Notification? = intent.getParcelableExtra(NOTIFICATION)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         val importance = NotificationManager.IMPORTANCE_HIGH
         val notificationChannel =
            NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
         assert(notificationManager != null)
         notificationManager.createNotificationChannel(notificationChannel)
      }
      val id = intent.getIntExtra(NOTIFICATION_ID, 0)
      assert(notificationManager != null)
      notificationManager.notify(id, notification)
   }
}