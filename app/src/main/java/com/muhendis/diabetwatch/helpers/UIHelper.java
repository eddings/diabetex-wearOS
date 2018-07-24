package com.muhendis.diabetwatch.helpers;

import android.app.Activity;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.muhendis.diabetwatch.R;

import java.util.Arrays;
import java.util.List;

public class UIHelper {
    Context context;
    private final String TAG="UIHelper";
    public static String strSeparator = ",";

    public UIHelper(Context context) {
        this.context = context;
    }

    public void showAlertInvalidUserNameOrPassword()
    {
        /*Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                "HATALI EPOSTA VEYA PAROLA");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("HATALI EPOSTA VEYA PAROLA");
        alertDialogBuilder.setIcon(R.drawable.logo);

        // set dialog message
        alertDialogBuilder
                .setMessage("Lütfen bilgilerinizi kontrol edip tekrar deneyiniz.")
                .setCancelable(false)
                .setPositiveButton("TAMAM",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity

                    }
                })
        ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showAlertNoInternet()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("İNTERNET BAĞLANTISI YOK");
        alertDialogBuilder.setIcon(R.drawable.logo);

        // set dialog message
        alertDialogBuilder
                .setMessage("Lütfen internet bağlantınızı kontrol edip tekrar deneyiniz.")
                .setCancelable(false)
                .setPositiveButton("TAMAM",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity

                    }
                })
        ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showAlertNoInternetNotCancelable(String title, String message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.logo);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showAlertConnectionProblem()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("BAĞLANTI PROBLEMİ");
        alertDialogBuilder.setIcon(R.drawable.logo);

        // set dialog message
        alertDialogBuilder
                .setMessage("Lütfen internet bağlantınızı kontrol edip tekrar deneyiniz.")
                .setCancelable(false)
                .setPositiveButton("TAMAM",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity

                    }
                })
        ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void syncMediaFiles(LocalDBHelper mLocalDBHelper, Activity activity){

        String[][] allLinks = mLocalDBHelper.getAllLinks();

        // If tehere is an image to download
        if(allLinks[0].length!=0)
        {
            Log.d(TAG,"ALL IMAGE LINK IS NOT EMPTY:"+allLinks[0].length);
            if(!isNetworkAvailable())
            {
                showAlertNoInternetNotCancelable("Program Verileri İndirilemedi","Yeni program verilerini indirmek için lütfen internete bağlı olduğunuzdan emin olup tekrar deneyiniz.");
            }
            else{
                ProgressBack imageProgress = new ProgressBack();
                imageProgress.activity = activity;
                String[] imageLinks = allLinks[0];
                String[] imageNames = allLinks[2];
                String[] imageDir = {"images"};
                imageProgress.execute(imageLinks,imageNames,imageDir);
            }
        }

        // If tehere is a video to download
        if(allLinks[1].length!=0)
        {
            Log.d(TAG,"ALL VIDEO LINK IS NOT EMPTY:"+allLinks[1].length);

            if(!isNetworkAvailable())
            {
                showAlertNoInternetNotCancelable("Program Verileri İndirilemedi","Yeni program verilerini indirmek için lütfen internete bağlı olduğunuzdan emin olup tekrar deneyiniz.");
            }
            else{
                ProgressBack videoProgress = new ProgressBack();
                videoProgress.activity = activity;
                String[] videoLinks = allLinks[1];
                String[] videoNames = allLinks[3];
                String[] videoDir = {"videos"};
                videoProgress.execute(videoLinks,videoNames,videoDir);
            }
        }

    }

    public static String convertListToString(List<String> list){
        String str = "";
        if(list!=null)
        {

            for (int i = 0;i<list.size(); i++) {
                str = str+list.get(i);
                // Do not append comma at the end of last element
                if(i<list.size()-1){
                    str = str+strSeparator;
                }
            }

        }
        return str;
    }
    public static List<String> convertStringToList(String str){
        String[] arr = str.split(strSeparator);
        return Arrays.asList(arr);
    }

    public void createPermanentNotificationForSpeed(String title, String body, int logo)
    {
        int drawableId=R.drawable.logo;

        if(logo==0){
            drawableId =R.drawable.up_arrow;
        }
        else if(logo==1){
            drawableId =R.drawable.success;
        }
        else{
            drawableId =R.drawable.down_arrow;
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                drawableId);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // The id of the channel.
            String id = context.getResources().getString(R.string.permanentNotificationChannelId);;
            // The user-visible name of the channel.
            CharSequence name = context.getResources().getString(R.string.permanentNotificationChannelName);
            // The user-visible description of the channel.
            String description = "Notificaiton Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.

            mChannel.setDescription(description);

            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            if(logo!=1){
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{1000,1000});
            }
            mNotificationManager.createNotificationChannel(mChannel);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, id)
                    .setLargeIcon(icon)
                    .setOngoing(true)
                    .setSmallIcon(drawableId)
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentText(body);


            Intent resultIntent = new Intent();
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

            NotificationManagerCompat mNotificationManagerCompat =

                    NotificationManagerCompat.from(context);

                    /*Notification note = mBuilder.build();
                    note.defaults |= Notification.DEFAULT_VIBRATE;
                    note.defaults |= Notification.DEFAULT_SOUND;*/
            mNotificationManagerCompat.notify(003, mBuilder.build());

        }
        else{
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setLargeIcon(icon)
                            .setSmallIcon(drawableId)
                            .setOngoing(true)
                            .setContentTitle(title)
                            //.setContentInfo(body)
                            .setContentText(body);
            if(logo!=1){
                mBuilder.setVibrate(new long[]{500,500,500});
            }

            Intent resultIntent = new Intent();

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            mNotificationManager.notify(003, mBuilder.build());
        }



    }

    public void cancelPermanentSpeedNotification(){
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManagerCompat mNotificationManagerCompat =

                    NotificationManagerCompat.from(context);

                    /*Notification note = mBuilder.build();
                    note.defaults |= Notification.DEFAULT_VIBRATE;
                    note.defaults |= Notification.DEFAULT_SOUND;*/
            mNotificationManagerCompat.cancel(003);

        }
        else{
            mNotificationManager.cancel(003);
        }
    }







}
