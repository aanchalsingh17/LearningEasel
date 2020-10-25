package com.example.learningeasle.admin;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.learningeasle.MainActivity;
import com.example.learningeasle.R;

public class NotificationReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    int Notification_Id = 1;
    Context context;
    private static final String CHANNEL_ID = "Channel";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String title = intent.getStringExtra("title");
        String action = intent.getStringExtra("Action");
        String msg;
        if(action.equals("passed")){
            msg = "Post passed by admin";
        }else{
            msg = "Post cancelled by admin";
        }
        notificationManager = NotificationManagerCompat.from(context);
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        Intent activityintent = new Intent(context, MainActivity.class);
        PendingIntent contentintent = PendingIntent.getActivity(context,0,activityintent,PendingIntent.FLAG_CANCEL_CURRENT);


       // notificationManager.notify(Notification_Id,notification);
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_action_account)
                .setContentTitle(title)
                .setContentText(msg)
                .setColor(Color.RED)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setOngoing(true)
                .setTimeoutAfter(5*60*60*1000)
                .setLights(255,6,7)
                .setContentIntent(contentintent)
//                        .setOnlyAlertOnce(true)
//                        .setAutoCancel(true)
                .build();

        notificationManager.notify(Notification_Id, notification);
    }

        @NonNull
        @TargetApi(26)
        private synchronized String createChannel() {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String name = "CUSTOM ";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            } else {
                //stopSelf();
            }
            return CHANNEL_ID;
        }

}
