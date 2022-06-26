package com.example.expesestracker.models;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.expesestracker.R;

import java.util.Calendar;

public class BuildNotifications {
    private static final String CHANNEL_ID = "Notify_CHANNEL";
    int notificationId = 1;
    private Context context;

    public BuildNotifications(Context context){
        this.context = context;
        createNotificationChannel();
    }

    public void buildNotification(String title, String body){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification
        // Here a incremental id is used
        notificationManager.notify(notificationId++, builder.build());
    }

    public void buildScheduledNotification( Notification notification){
            String NOTIFICATION_ID = "notification-id";
            String NOTIFICATION = "notification";

            Intent notificationIntent = new Intent( context, MyNotificationPublisher. class ) ;
            notificationIntent.putExtra(NOTIFICATION_ID, 1 ) ;
            notificationIntent.putExtra(NOTIFICATION , notification) ;
            PendingIntent pendingIntent = PendingIntent. getBroadcast ( context, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE) ;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context. ALARM_SERVICE ) ;
            assert alarmManager != null;

//            To get this weeks sunday
            Calendar c=Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            c.add(Calendar.DATE,7);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    public Notification getNotification (String title, String body) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel( true ) ;
        builder.setChannelId( CHANNEL_ID ) ;
        return builder.build() ;

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}
