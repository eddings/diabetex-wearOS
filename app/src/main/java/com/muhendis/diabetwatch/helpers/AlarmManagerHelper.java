package com.muhendis.diabetwatch.helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import com.google.firebase.FirebaseApp;
import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.activities.ProgramActivity;
import com.muhendis.diabetwatch.db.DiabetWatchDbHelper;

import java.util.Calendar;

/**
 * Created by muhendis on 1.04.2018.
 */

public class AlarmManagerHelper extends BroadcastReceiver {
    private final String TAG ="MYALARM";
    private LocalDBHelper localDbHelper;
    private DiabetWatchDbHelper diabetWatchDbHelper;
    public AlarmManagerHelper() {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        FirebaseApp.initializeApp(context);

        diabetWatchDbHelper = new DiabetWatchDbHelper(context);
        localDbHelper = new LocalDBHelper(diabetWatchDbHelper,null);

        if (intent.getAction()!= null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAlarms(context);
        }
        //NotificationCompat.Builder mBuilder;
        String message = intent.getStringExtra(Keys.ALARM_MESSAGE);
        boolean showIfNotFinishedProgram = intent.getBooleanExtra(Keys.ALARM_MESSAGE_SHOW_IF_NOT_FINISHED_PROGRAM,false);
        boolean showIfFinishedProgram = intent.getBooleanExtra(Keys.ALARM_MESSAGE_SHOW_IF_FINISHED_PROGRAM,false);
        boolean showIfFinishedProgramInEvening = intent.getBooleanExtra(Keys.ALARM_MESSAGE_SHOW_IF_NOT_FINISHED_PROGRAM_IN_EVENING,false);
        boolean showStatisticsInFriday = intent.getBooleanExtra(Keys.ALARM_MESSAGE_SHOW_STATISTICS_IN_FRIDAY,false);


        Log.d(TAG,"LAST 5 DAYS FINISHED PROGRAMS: "+localDbHelper.checkFinishedProgramNumberForLastFiveDays());
        if(showIfFinishedProgram && localDbHelper.isAllProgramFinishedToday()){
            message="Tebrikler. Bugün çok sağlıklısın \uD83D\uDC4F\uD83D\uDE0A";
        }

        if(showIfFinishedProgramInEvening && localDbHelper.isAllProgramFinishedToday()){
            message="Tebrikler. Bütün egzersizlerini tamamladın. Bugün çok sağlıklısın \uD83D\uDC4F\uD83D\uDE0A";
        }

        if(showStatisticsInFriday)
        {
            int numberOfDaysProgramsCompleted = localDbHelper.checkFinishedProgramNumberForLastFiveDays();
            message="Bu hafta "+ numberOfDaysProgramsCompleted+" gün programlarının tümünü tamamladın.";
        }

        Log.d(TAG,"MESSAGE: "+message);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.logo);

        if(message!=null) {

            if ((showIfNotFinishedProgram && !localDbHelper.isAllProgramFinishedToday()) || (!showIfNotFinishedProgram)) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "my_channel_01";
                    String description = "my_channel_01_desc";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(context.getResources().getString(R.string.default_notification_channel_id), name, importance);
                    channel.setDescription(description);
                    //channel.enableLights(true);
                    //Sets the notification light color for notifications posted to this channel, if the device supports this feature.
                    //channel.setLightColor(Color.BLUE);

                    //channel.setShowBadge(true);
                    channel.setVibrationPattern(new long[]{1000,1000});
                    channel.enableVibration(true);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);


                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "8888")
                            .setLargeIcon(icon)
                            .setBadgeIconType(R.drawable.logo)
                            .setSmallIcon(R.drawable.logo_white)
                            .setContentTitle("Diabetex")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText(message);


                    Intent resultIntent = new Intent(context, ProgramActivity.class);
                    int _id = (int) System.currentTimeMillis();
                    // Because clicking the notification opens a new ("special") activity, there's
                    // no need to create an artificial back stack.
                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    _id,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);


                    // Gets an instance of the NotificationManager service//

                    NotificationManagerCompat mNotificationManager =

                            NotificationManagerCompat.from(context);

                    /*Notification note = mBuilder.build();
                    note.defaults |= Notification.DEFAULT_VIBRATE;
                    note.defaults |= Notification.DEFAULT_SOUND;*/
                    mNotificationManager.notify(001, mBuilder.build());
                } else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setLargeIcon(icon)
                            .setBadgeIconType(R.drawable.logo)
                            .setSmallIcon(R.drawable.logo_white)
                            .setContentTitle("Diabetex")
                            .setSound(alarmSound)
                            .setVibrate(new long[]{2000, 2000})
                            .setContentText(message);


                    Intent resultIntent = new Intent(context, ProgramActivity.class);
                    int _id = (int) System.currentTimeMillis();
                    // Because clicking the notification opens a new ("special") activity, there's
                    // no need to create an artificial back stack.
                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    _id,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);


                    // Gets an instance of the NotificationManager service//

                    NotificationManagerCompat mNotificationManager =

                            NotificationManagerCompat.from(context);

                    Notification note = mBuilder.build();
                    note.defaults |= Notification.DEFAULT_VIBRATE;
                    note.defaults |= Notification.DEFAULT_SOUND;
                    mNotificationManager.notify(001, note);
                }


            }

        }



    }





    public void setAlarms(final Context context){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AlarmManagerHelper alarm = new AlarmManagerHelper();

                AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, AlarmManagerHelper.class);
                i.putExtra(Keys.ALARM_MESSAGE,"Günaydın. Egzersiz yapmak için güzel bir gün ☀️☀️");

                PendingIntent pi = PendingIntent.getBroadcast(context, 930, i, 0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 30);

                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);


                ////////////////////////////////////////////////////////////////////////////////////

                am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                i = new Intent(context, AlarmManagerHelper.class);
                i.putExtra(Keys.ALARM_MESSAGE,"Egzersizlere başladık mı? \uD83C\uDFC3\uD83D\uDC4D️");
                i.putExtra(Keys.ALARM_MESSAGE_SHOW_IF_NOT_FINISHED_PROGRAM,true);
                pi = PendingIntent.getBroadcast(context, 1300, i, 0);
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 13);
                calendar.set(Calendar.MINUTE, 00);

                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);

                ////////////////////////////////////////////////////////////////////////////////////

                am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                i = new Intent(context, AlarmManagerHelper.class);
                i.putExtra(Keys.ALARM_MESSAGE,"Hala bitirilmemiş egzersizlerin var. Yapabilirsin \uD83D\uDCE3\uD83D\uDC4A\uD83D\uDC4A️");
                i.putExtra(Keys.ALARM_MESSAGE_SHOW_IF_FINISHED_PROGRAM,true);
                pi = PendingIntent.getBroadcast(context, 1830, i, 0);
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 18);
                calendar.set(Calendar.MINUTE, 30);

                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);

                ////////////////////////////////////////////////////////////////////////////////////

                am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                i = new Intent(context, AlarmManagerHelper.class);
                i.putExtra(Keys.ALARM_MESSAGE,"Haydi bir gayret daha bitir egzersizlerini \uD83D\uDC4C\uD83D\uDC4C\uD83D\uDCAA️");
                i.putExtra(Keys.ALARM_MESSAGE_SHOW_IF_NOT_FINISHED_PROGRAM_IN_EVENING,true);
                pi = PendingIntent.getBroadcast(context, 2100, i, 0);
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 21);
                calendar.set(Calendar.MINUTE, 00);

                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);

                ////////////////////////////////////////////////////////////////////////////////////

                am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                i = new Intent(context, AlarmManagerHelper.class);
                i.putExtra(Keys.ALARM_MESSAGE,"Haydi bir gayret daha bitir egzersizlerini \uD83D\uDC4C\uD83D\uDC4C\uD83D\uDCAA️");
                i.putExtra(Keys.ALARM_MESSAGE_SHOW_STATISTICS_IN_FRIDAY,true);
                pi = PendingIntent.getBroadcast(context, 2130, i, 0);
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.DAY_OF_WEEK,6);
                calendar.set(Calendar.HOUR_OF_DAY, 21);
                calendar.set(Calendar.MINUTE, 30);

                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY*7, pi);


            }
        });

        t.start();


    }

}
