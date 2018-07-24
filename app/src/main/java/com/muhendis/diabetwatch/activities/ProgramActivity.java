package com.muhendis.diabetwatch.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.drawer.WearableActionDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.adapters.ProgramAdapter;
import com.muhendis.diabetwatch.adapters.ProgramWearableAdapter;
import com.muhendis.diabetwatch.callbacks.CustomScrollingLayoutCallback;
import com.muhendis.diabetwatch.db.DiabetWatchDbHelper;
import com.muhendis.diabetwatch.helpers.AlarmManagerHelper;
import com.muhendis.diabetwatch.helpers.FirebaseDBHelper;
import com.muhendis.diabetwatch.helpers.LocalDBHelper;
import com.muhendis.diabetwatch.helpers.UIHelper;
import com.muhendis.diabetwatch.model.ProgramFirebaseDb;

import android.support.wear.widget.WearableRecyclerView;
import android.widget.Toast;

import java.security.Permission;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProgramActivity extends WearableActivity implements
        MenuItem.OnMenuItemClickListener {

    private final String TAG="ProgramActivity";
    private WearableActionDrawerView mWearableActionDrawer;
    private FirebaseDBHelper mFirebaseDBHelper;
    private RecyclerView mRecyclerView;
    private ProgramAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private WearableRecyclerView mWearableRecyclerView;
    private LocalDBHelper mLocalDBHelper;
    private DiabetWatchDbHelper mDbHelper;
    private ProgramWearableAdapter mProgramAdapter;
    private UIHelper mUIHelper;
    private ProgramFirebaseDb[] mAllPrograms;
    private final int MY_PERMISSIONS_REQUEST_BODY_SENSOR = 1;
    private AlarmManagerHelper alarmManagerHelper;
    private String notificationBody,notificationTitle,notificationMessageId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        updateToken();
        // Enables Always-on
        setAmbientEnabled();

        permissionRequest();

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG,"KEY: "+key+" Value: "+value);

            }
        }

        Intent intent = getIntent();
        notificationBody = intent.getStringExtra(Keys.NOTIFICATION_MESSAGE_BODY);
        notificationTitle = intent.getStringExtra(Keys.NOTIFICATION_MESSAGE_TITLE);
        notificationMessageId = intent.getStringExtra(Keys.NOTIFICATION_MESSAGE_ID);

        if(notificationBody!=null && notificationTitle!=null){

            showNotificationAlertFromFcm(notificationTitle,notificationBody);
        }
        else{
            Log.d(TAG,"EMPTY NOTIFICATION DATA");
        }


        alarmManagerHelper = new AlarmManagerHelper();
        alarmManagerHelper.setAlarms(getApplicationContext());

        mUIHelper = new UIHelper(this);
        mDbHelper = new DiabetWatchDbHelper(getApplicationContext());
        mFirebaseDBHelper = new FirebaseDBHelper(getApplicationContext(),this);
        mLocalDBHelper = new LocalDBHelper(mDbHelper, this);

        mAllPrograms = mLocalDBHelper.getAllPrograms();
        mFirebaseDBHelper.syncLocalStatisticsWithFirebase(getUid());

        //setupRecylerView();
        setupWearableRecylerView();
        removeCompletedProgramsFromDB();

        mWearableActionDrawer = findViewById(R.id.bottom_action_drawer);
        // Peeks action drawer on the bottom.
        mWearableActionDrawer.getController().peekDrawer();
        mWearableActionDrawer.setOnMenuItemClickListener(this);
    }

    public void permissionRequest(){

        isAccesNetworkPermissionGranted();
        isStoragePermissionGranted();

        if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.BODY_SENSORS},
                    MY_PERMISSIONS_REQUEST_BODY_SENSOR);
        }
        else{
            Log.d(TAG,"ALREADY GRANTED");
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my_channel_01";
            String description = "my_channel_01_desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("8888", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.logo);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),"8888")
                    .setLargeIcon(icon)
                    .setBadgeIconType(R.drawable.logo)
                    .setSmallIcon(R.drawable.logo_white)
                    .setContentTitle("Diabetex")
                    .setSound(alarmSound)
                    .setVibrate(new long[] { 1000, 1000})
                    .setContentText("Message");



            Intent resultIntent = new Intent(getApplicationContext(), ProgramActivity.class);
            int _id = (int) System.currentTimeMillis();
            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            _id,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            // Gets an instance of the NotificationManager service//

            NotificationManagerCompat mNotificationManager  =

                    NotificationManagerCompat.from(getApplicationContext());

            mNotificationManager.notify(001, mBuilder.build());

        }
    }

    public void createNotificationTest(){
        createNotificationChannel();
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());
        boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();

        // If notifications are disabled, allow user to enable.
        if (!areNotificationsEnabled) {
            Log.d(TAG,"NOTIFICATION NOT ENABLED");
        }
        else{
            Log.d(TAG,"NOTIFICATION ENABLED");
        }
        int notificationId = 888;

        String id = "my_channel_01";
        Intent viewIntent = new Intent(this, ProgramActivity.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), id)
                        .setSmallIcon(R.drawable.program_icon)
                        .setContentTitle("Test")
                        .setContentText("test content")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service


        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public  boolean isAccesNetworkPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }



    public String getUid(){
        SharedPreferences sharedPref = this.getSharedPreferences(getResources().getString(R.string.saved_user_file_key), Context.MODE_PRIVATE);
        int uid = sharedPref.getInt(getString(R.string.saved_user_uid_key), 0);
        return String.valueOf(uid);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuProgram:

                return true;
            case R.id.menuExit:
                logout_user();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void syncDataWithFirebase(){
        mFirebaseDBHelper.syncDataWithLocalDB(this,mProgramAdapter);
    }
    public void logout_user(){
        Intent intent  = new Intent(this,LoginActivity.class);
        intent.putExtra(getResources().getString(R.string.logging_out_key),true);
        startActivity(intent);
        this.finish();
    }

    public void setupRecylerView(){
        mRecyclerView = findViewById(R.id.program_recycler_view);
        if (mAllPrograms.length != 0) {
            findViewById(R.id.noProgramFrameLayout).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.noProgramFrameLayout).setVisibility(View.VISIBLE);
        }
        mAdapter = new ProgramAdapter(mAllPrograms,this);
        mAdapter.setLocalDbHelper(mLocalDBHelper);



        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        mRecyclerView.setItemViewCacheSize(30);
        //String[] dataset = {"Test1 test test test test test test test test test test"};
        mRecyclerView.setAdapter(mProgramAdapter);

        syncDataWithFirebase();
    }

    public void setupWearableRecylerView(){

        mWearableRecyclerView = findViewById(R.id.program_recycler_view);
        if (mAllPrograms.length != 0) {
            findViewById(R.id.noProgramFrameLayout).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.noProgramFrameLayout).setVisibility(View.VISIBLE);
        }
        mProgramAdapter = new ProgramWearableAdapter(mAllPrograms,this);
        mProgramAdapter.setLocalDbHelper(mLocalDBHelper);

        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();

        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        mWearableRecyclerView.setItemViewCacheSize(30);
        //String[] dataset = {"Test1 test test test test test test test test test test"};
        mWearableRecyclerView.setAdapter(mProgramAdapter);

        syncDataWithFirebase();
    }

    public void removeCompletedProgramsFromDB(){
        List<String> pidsToRemove = new ArrayList<String>();

        for (ProgramFirebaseDb program :
                mAllPrograms) {
            SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = mdformat.parse(program.getFinishDate());
                if(System.currentTimeMillis()>date.getTime()){
                    mLocalDBHelper.deleteProgram(program.getPid());
                    ProgramFirebaseDb[] ap = mLocalDBHelper.getAllPrograms();
                    if (ap.length != 0) {
                        findViewById(R.id.noProgramFrameLayout).setVisibility(View.GONE);
                    }
                    else{
                        findViewById(R.id.noProgramFrameLayout).setVisibility(View.VISIBLE);
                    }
                    mProgramAdapter.setAdapter(ap);
                    pidsToRemove.add(program.getPid());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if(pidsToRemove.size()>0)
        {
            mFirebaseDBHelper.removeCompletedPrograms(pidsToRemove);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
        {
            mProgramAdapter.notifyDataSetChanged();
        }
        mProgramAdapter.notifyDataSetChanged();
        mUIHelper.syncMediaFiles(mLocalDBHelper,this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BODY_SENSOR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                else {
                    Toast.makeText(this,"Devam edebilmek için sensör verilerine izin vermeniz gereklidir. Lütfen Ayarlar > Apps > Diabetex > Permissions > Sensor verisini aktif ediniz.",Toast.LENGTH_LONG).show();
                    this.finish();
                }
                return;

            }


            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void showNotificationAlertFromFcm(String title,String message)
    {
        Log.d(TAG,"SHOWING NOTIFICATION");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mFirebaseDBHelper.updateFcmMessageRead(notificationMessageId);
                    }
                })
                .setCancelable(false);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void updateToken(){
        FirebaseDatabase.getInstance().getReference().child("users/"+getUid()+"/token").setValue(FirebaseInstanceId.getInstance().getToken());

    }


}
