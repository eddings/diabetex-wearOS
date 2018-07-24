package com.muhendis.diabetwatch.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.muhendis.diabetwatch.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProgressBack extends AsyncTask<String[],String[],String> {
    ProgressDialog PD;
    AlertDialog dialog;
    public Activity activity;
    static int downloadedFolderCounter=0;
    @Override
    protected String doInBackground(String[]... urls) {
        File rootFile = new File(activity.getExternalFilesDir(null)
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/Files/"+urls[2][0]);

        // Create the storage directory if it does not exist
        if (! rootFile.exists()){
            rootFile.mkdirs();
        }

        for (int i=0;i<urls[0].length;i++) {
            if(!downloadFile(urls[0][i],urls[1][i],urls[2][0])){
                return "error";
            }
        }
        return "ok";
    }

    @Override
    protected void onPreExecute() {

        //PD= ProgressDialog.show(MainActivity.this,"Veriler İndiriliyor", "Lütfen Bekleyiniz. Program verileri indiriliyor. Videoların indirilmesi zaman alabilir.", true);
        //PD.setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View custom_dialog = inflater.inflate(R.layout.custom_dialog_downloading, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(custom_dialog)
                .setCancelable(false)
                .setTitle("Veriler İndiriliyor");
        dialog = builder.create();
        dialog.show();

    }

    protected void onPostExecute(String result) {
        dialog.dismiss();
        //PD.dismiss();
        if(result=="ok")
        {
            downloadedFolderCounter++;
            setLinksDownloaded(true);
            if(downloadedFolderCounter==2){
                downloadedFolderCounter=0;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                // Get the layout inflater
                LayoutInflater inflater = activity.getLayoutInflater();
                final View custom_dialog = inflater.inflate(R.layout.custom_dialog_downloading_success, null);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(custom_dialog)
                        .setCancelable(true)
                        .setTitle("Veriler İndirildi")
                        .setPositiveButton("TAMAM",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity

                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        else{
            setLinksDownloaded(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            // Get the layout inflater
            LayoutInflater inflater = activity.getLayoutInflater();
            final View custom_dialog = inflater.inflate(R.layout.custom_dialog_downloading_fail, null);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(custom_dialog)
                    .setCancelable(true)
                    .setTitle("Veriler Sonra İndirilecek")
                    .setPositiveButton("TAMAM",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity

                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }


    }

    private boolean downloadFile(String fileURL, String fileName, String dir) {
        try {
            File rootFile = new File(activity.getExternalFilesDir(null)
                    + "/Android/data/"
                    + activity.getApplicationContext().getPackageName()
                    + "/Files/"+dir);

            // Create the storage directory if it does not exist
            if (! rootFile.exists()){
                if (! rootFile.mkdirs()){
                    return false;
                }
            }

            /*String rootDir = activity.getExternalFilesDir(null)
                    + File.separator + "diabetex/"+dir;

            File rootFile = new File(rootDir);
            rootFile.mkdirs();*/
            URL url = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(rootFile,
                    fileName));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
            return true;
        } catch (Exception e) {
            Log.d("Error....", e.toString());
            return false;
        }

    }


    public void setLinksDownloaded(boolean isDownloaded){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.saved_user_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getString(R.string.saved_user_all_videos_downloaded_key), isDownloaded);
        editor.apply();
    }


}



