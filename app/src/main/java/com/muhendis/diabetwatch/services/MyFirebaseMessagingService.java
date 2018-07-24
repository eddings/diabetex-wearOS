package com.muhendis.diabetwatch.services;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.activities.LoginActivity;
import com.muhendis.diabetwatch.activities.ProgramActivity;

/**
 * Created by muhendis on 24.11.2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String TAG = "MyFirebaseMS";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("body"));

            String body = remoteMessage.getData().get("body");
            String title = remoteMessage.getData().get("title");
            String messageId = remoteMessage.getData().get("messageId");
            create_notification(title,body,messageId);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void showMessageAsAlert(String title, String message, String messageId)
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getApplicationContext());

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void create_notification(String title, String body, String messageId)
    {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my_channel_01";
            String description = "my_channel_01_desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelId = getResources().getString(R.string.default_notification_channel_id);
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            //channel.enableLights(true);
            //Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            //channel.setLightColor(Color.BLUE);

            //channel.setShowBadge(true);
            channel.setVibrationPattern(new long[]{1000,1000});
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                    .setLargeIcon(icon)
                    .setBadgeIconType(R.drawable.logo)
                    .setSmallIcon(R.drawable.logo_white)
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentText(body);


            Intent resultIntent = new Intent(this, LoginActivity.class);
            int _id = (int) System.currentTimeMillis();
            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            _id,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            // Gets an instance of the NotificationManager service//

            NotificationManagerCompat mNotificationManager =

                    NotificationManagerCompat.from(this);

                    /*Notification note = mBuilder.build();
                    note.defaults |= Notification.DEFAULT_VIBRATE;
                    note.defaults |= Notification.DEFAULT_SOUND;*/
            mNotificationManager.notify(002, mBuilder.build());
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(icon)
                    .setBadgeIconType(R.drawable.logo)
                    .setSmallIcon(R.drawable.logo_white)
                    .setContentTitle(title)
                    .setSound(alarmSound)
                    .setVibrate(new long[]{2000, 2000})
                    .setContentText(body);


            Intent resultIntent = new Intent(this, LoginActivity.class);
            int _id = (int) System.currentTimeMillis();
            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            _id,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            // Gets an instance of the NotificationManager service//

            NotificationManagerCompat mNotificationManager =

                    NotificationManagerCompat.from(this);

            Notification note = mBuilder.build();
            note.defaults |= Notification.DEFAULT_VIBRATE;
            note.defaults |= Notification.DEFAULT_SOUND;
            mNotificationManager.notify(002, note);
        }

    }
}
