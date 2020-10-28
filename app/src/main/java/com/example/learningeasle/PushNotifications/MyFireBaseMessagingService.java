package com.example.learningeasle.PushNotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.learningeasle.chats.ChatDetailsActivity;
import com.example.learningeasle.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class MyFireBaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final String title, message;
        super.onMessageReceived(remoteMessage);


        title = remoteMessage.getData().get("Title");
        final String[] data = new String[2];
        int i = 0;
        data[0] = "";
        data[1] = "";
        while (i < title.length() && title.charAt(i) != '+') {
            data[0] += title.charAt(i);
            i++;
        }
        if (i + 4 < title.length())
            data[1] = title.substring(i + 4);
        final String name = data[0];


        message = remoteMessage.getData().get("Message");

        Drawable myDrawable = getResources().getDrawable(R.drawable.logo_large);
        final Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();

        System.out.println(" in  fcm");

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), "Notify", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.tabSelected));
            notificationChannel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (data[0].length() >= 5 && data[0].substring(0, 5).equals("Admin")) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.default_notification_channel_id))
                    .setContentTitle(name)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.logo_large)
                    .setLargeIcon(bitmap);

            notificationManager.notify((int) (new Date().getTime() / 1000) % Integer.MAX_VALUE, notificationBuilder.build());
        } else {
            Intent resultIntent = new Intent(this, ChatDetailsActivity.class);
            resultIntent.putExtra("Id", data[1]);
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // Get the PendingIntent containing the entire back stack
            final PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.default_notification_channel_id))
                    .setContentTitle(name)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.logo_large)
                    .setLargeIcon(bitmap)
                    .setContentIntent(resultPendingIntent);

            notificationManager.notify((int) (new Date().getTime() / 1000) % Integer.MAX_VALUE, notificationBuilder.build());

        }

    }


}
