package com.example.learningeasle.admin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.NotificationCompat;

import com.example.learningeasle.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String action = intent.getStringExtra("Action");
        notificationManager = NotificationManagerCompat.from(context);
        Intent activityintent = new Intent(context, MainActivity.class);
        PendingIntent contenintent = PendingIntent.getActivity(context,0,activityintent,PendingIntent.FLAG_CANCEL_CURRENT);
        //Notification notification = NotificationCompat.b
    }
}
