package com.example.expesestracker.models

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.expesestracker.R
import java.util.*

class ScheduleNotification(private var context: Context, var applicationContext: Context) {
    private val CHANNEL_ID = "NOTIFY_SCHEDULE_CHANNEL"

    fun scheduleNotification()
    {
        createNotificationChannel()
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Your Weekly Report"
        val message = "Lorem"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val cal = Calendar.getInstance()
//        val year = cal[Calendar.YEAR]
//        cal[Calendar.DAY_OF_WEEK] = cal.MONDAY
//        val firstWkDay = java.lang.String.valueOf(cal.time)
//        //cal.set(Calendar.DAY_OF_WEEK, cal.SUNDAY);
//        //cal.set(Calendar.DAY_OF_WEEK, cal.SUNDAY);
        cal.add(Calendar.DAY_OF_WEEK, 6)
//        val lastWkDay = java.lang.String.valueOf(cal.time)

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
//        val rightNow = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY


        val time = cal.timeInMillis

//        For Testing will show a notification on app load
//        val time = Calendar.getInstance().timeInMillis

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.schedule_channel_name)
            val description: String = context.getString(R.string.schedule_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager: NotificationManager =
                context.getSystemService<NotificationManager>(
                    NotificationManager::class.java
                )
            notificationManager.createNotificationChannel(channel)
        }
    }
}