package com.muhendis.diabetwatch.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.db.DiabetWatchDbHelper;
import com.muhendis.diabetwatch.helpers.FirebaseDBHelper;
import com.muhendis.diabetwatch.helpers.LocalDBHelper;
import com.muhendis.diabetwatch.helpers.UIHelper;
import com.muhendis.diabetwatch.model.ProgramExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.StatisticsExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.StatisticsProgramFirebaseDb;
import com.muhendis.diabetwatch.services.LocationService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExerciseDetailsActivity extends WearableActivity implements SensorEventListener {

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 2000;

    TextView mHeader, mInstruction, mSet, mRep, mRest, mDailyRep, mWeeklyRep;
    String eid, pid, videoLink, photoLink;
    DiabetWatchDbHelper mDbHelper;
    LocalDBHelper mLocalDbHelper;
    ProgramExerciseFirebaseDb mExercise;
    FirebaseDBHelper mFirebaseDbHelper;
    UIHelper mUIHelper;
    ImageButton mDoneButton, mVideoPlay;
    final ExerciseDetailsActivity activity = this;
    SensorManager mSensorManager;
    Sensor mStepCounter, mHeartRate;
    int walkedDistance=0,stepCounter = 0, numberOfMeasurement = 0, heartRateFinal = 0, startingStepNumber = 0, numberOfStepSensorDetected = 0;
    List<String> heartRate;
    long startTime = 0, elapsedTime = 0;
    private ProgressDialog pdLoading;
    private boolean isProgramfinished = false;
    private EditText pulseEditText;
    private final String TAG = "ExerciseDetailsActivity";
    LocationManager locationManager;
    static final int OPEN_LOCATION_SETTINGS_REQUEST = 1;
    static final int OPEN_VIDEO_PLAYER_REQUEST = 0;
    static final int OPEN_LOCATION_PERMISSION_REQUEST = 2;
    List<String> walkingSpeeds;
    Location startLocation;
    Intent startIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);


        // Enables Always-on
        setAmbientEnabled();



        heartRate = new ArrayList<String>();
        walkingSpeeds = new ArrayList<String>();


        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            Log.d(TAG,"------FEATURE_SENSOR_STEP_COUNTER EXISTS");
        }
        else{
            Log.d(TAG,"------FEATURE_SENSOR_STEP_COUNTER DOES NOT EXISTS");
        }

        if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
            Log.d(TAG,"------FEATURE_SENSOR_STEP_DETECTOR EXISTS");
        }
        else{
            Log.d(TAG,"------FEATURE_SENSOR_STEP_DETECTOR DOES NOT EXISTS");
        }
        if(isKitKatWithStepCounter(getPackageManager())){
            mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this,mStepCounter,SensorManager.SENSOR_DELAY_FASTEST);
        }
        else{
            mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            mSensorManager.registerListener(this,mStepCounter,SensorManager.SENSOR_DELAY_FASTEST);
        }

        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this,mHeartRate,SensorManager.SENSOR_DELAY_FASTEST);


        startTime = System.currentTimeMillis();

        mDoneButton = findViewById(R.id.exerciseDoneButton);
        mDbHelper = new DiabetWatchDbHelper(getApplicationContext());
        mLocalDbHelper = new LocalDBHelper(mDbHelper,this);
        mUIHelper = new UIHelper(this);
        mFirebaseDbHelper = new FirebaseDBHelper(getApplicationContext(),this);

        eid = getIntent().getStringExtra(Keys.EX_ID);
        pid = getIntent().getStringExtra(Keys.PID_KEY);

        if(mLocalDbHelper.isExerciseFinishedToday(eid,pid)){
            mDoneButton.setVisibility(View.GONE);
        }

        mExercise = mLocalDbHelper.getExercise(pid,eid);

        Log.d(TAG,"EXERCISE DETAILS: "+mExercise.getName()+mExercise.getSets());

        mHeader = findViewById(R.id.exHeader);
        mInstruction = findViewById(R.id.exDetailsExpEditText);
        mSet = findViewById(R.id.exerciseSets);
        mRep = findViewById(R.id.exerciseRep);
        mRest = findViewById(R.id.exerciseRest);
        mDailyRep = findViewById(R.id.exerciseDailyRep);
        mWeeklyRep = findViewById(R.id.exerciseWeeklyRep);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elapsedTime = (System.currentTimeMillis()-startTime)/1000;
                showExerciseDone();
            }
        });

        mHeader.setText(mExercise.getName());
        mInstruction.setText(mExercise.getInstruction());
        mSet.setText(String.valueOf(mExercise.getSets()));
        mRep.setText(String.valueOf(mExercise.getReps()));
        mRest.setText(String.valueOf(mExercise.getHold())+" sn");
        mDailyRep.setText(String.valueOf(mExercise.getDailyRep()));
        mWeeklyRep.setText(String.valueOf(mExercise.getWeeklyRep()));
        videoLink = mExercise.getVideoLink();
        photoLink = mExercise.getPhotoLink();


        mVideoPlay = findViewById(R.id.exDetailsVideoPlayImage);
        implementPlayImageOnClickListener();

        checkAndSetupForWalkingExercise();

    }

    private void implementPlayImageOnClickListener()
    {
        mVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String videoFilePath = activity.getExternalFilesDir(null)
                        + "/Android/data/"
                        + getApplicationContext().getPackageName()
                        + "/Files/videos/"+pid+"_"+eid+".mp4";
                String imageFilePath = activity.getExternalFilesDir(null)
                        + "/Android/data/"
                        + getApplicationContext().getPackageName()
                        + "/Files/images/"+pid+"_"+eid+".gif";
                Intent intent = new Intent(getApplicationContext(),ExerciseVideoActivity.class);
                intent.putExtra(Keys.VIDEO_KEY,videoLink);
                intent.putExtra(Keys.LOCAL_VIDEO_KEY,videoFilePath);
                intent.putExtra(Keys.IMAGE_KEY,photoLink);
                intent.putExtra(Keys.LOCAL_IMAGE_KEY,imageFilePath);
                activity.startActivityForResult(intent,OPEN_VIDEO_PLAYER_REQUEST);

            }
        });
    }

    private void checkAndSetupForWalkingExercise(){
        /*
         * Register Location serivce if it is walking exercise
         * */

        if(mExercise.getIsWalking()){

            ImageButton exerciseVideoPlayImage = findViewById(R.id.exDetailsVideoPlayImage);
            exerciseVideoPlayImage.setVisibility(View.GONE);

            // Use and change set and rep texts for minimum and maximum speed to inform user
            findViewById(R.id.exerciseDetailsRestLinearLayout).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.exerciseDetailsSetHeader)).setText(getResources().getString(R.string.exerciseDetailsActivityMinWalkingSpeedTitle));
            ((TextView)findViewById(R.id.exerciseDetailsRepHeader)).setText(getResources().getString(R.string.exerciseDetailsActivityMaxWalkingSpeedTitle));
            ((TextView)findViewById(R.id.exerciseSets)).setText(mExercise.getMinWalkingSpeed()+" m/sn");
            ((TextView)findViewById(R.id.exerciseRep)).setText(mExercise.getMaxWalkingSpeed()+" m/sn");


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},OPEN_LOCATION_PERMISSION_REQUEST);
            }

            isLocationEnabled();
        }
        else{
            Log.d(TAG,"NOT WALKING EXERCISE");
        }
    }
    private void isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d(TAG,"locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)  FALSE");
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Konum Servislerini Etkinleştir");
            alertDialog.setMessage("Yürüme egzersizini yapabilmek için lütfen konum servislerinizi etkinleştiriniz.");
            alertDialog.setPositiveButton("Konum Servisi Etkinleştir", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent,OPEN_LOCATION_SETTINGS_REQUEST);
                    dialog.dismiss();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
        else{
            startIntent = new Intent(this, LocationService.class);
            startIntent.putExtra(getResources().getString(R.string.minimumSpeedIntentKey),mExercise.getMinWalkingSpeed());
            startIntent.putExtra(getResources().getString(R.string.maximumSpeedIntentKey),mExercise.getMaxWalkingSpeed());

            //startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(startIntent);
            }
            else{
                startService(startIntent);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
        else{
            Toast.makeText(this, "Lütfen yürüme egzersizini yapabilmek için konum kullanmaya izin veriniz", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},OPEN_LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(startIntent!=null){
            stopService(startIntent);
        }

        if(mSensorManager!=null){
            mSensorManager.unregisterListener(this);
        }
    }

    public void showExerciseDone()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Egzersizi bitirdim");
        alertDialogBuilder.setIcon(R.drawable.done_white);

        //final Context context = getApplicationContext();
        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("TAMAM",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // Calculate the min time which should pass to finish this exercise
                        int minTimeForExercise = mExercise.getDuration()*mExercise.getReps()*mExercise.getSets();

                        // Check if spent time for this exercise is enough
                        // If not don not let user to finish exercise
                        if((int)elapsedTime<minTimeForExercise)
                        {
                            dialog.dismiss();
                            mUIHelper.showSimpleAlertWithButton(getResources().getString(R.string.spentTimeForExerciseSoLowTitle),getResources().getString(R.string.spentTimeForExerciseSoLowMessage),getResources().getString(R.string.spentTimeForExerciseSoLowBtnText));
                        }
                        else{
                            // Finish exercise
                            if(mSensorManager!=null){
                                mSensorManager.unregisterListener(activity);
                            }
                            walkingSpeeds = LocationService.getInstance().walkingSpeeds;
                            walkedDistance = LocationService.getInstance().walkedDistance;
                            StatisticsExerciseFirebaseDb statisticsExerciseFirebaseDb = new StatisticsExerciseFirebaseDb(getCurrentDate(), mExercise.getEid(),mExercise.getPid(),mExercise.getUid(),(int)elapsedTime,stepCounter,heartRate,walkingSpeeds,mExercise.getIsWalking(),walkedDistance);
                            mLocalDbHelper.insertStatisticsExercise(statisticsExerciseFirebaseDb,mFirebaseDbHelper);
                            if(startIntent!=null){
                                stopService(startIntent);
                            }
                            if(mLocalDbHelper.isAllExerciseFinishedForProgram(mExercise.getPid()))
                            {
                                dialog.dismiss();
                                showProgramFinished(mLocalDbHelper,mExercise.getPid());
                            }
                            else
                            {
                                setResult(Keys.FINISHED_EXERCISE_RESULT_CODE);
                                finish();
                            }
                        }


                    }
                })
                .setNegativeButton("VAZGEÇ",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
        ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showProgramFinished(LocalDBHelper mLocalDBHelper, String pid){
        final LocalDBHelper mLocalDbHelper = mLocalDBHelper;
        final String currentPid = pid;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View custom_dialog = inflater.inflate(R.layout.custom_dialog, null);
        pulseEditText = custom_dialog.findViewById(R.id.pulse);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(custom_dialog)
                .setCancelable(false)
                .setTitle("Tebrikler programınızı tamamladınız.")
                .setMessage("Lütfen aşağıdaki değerleri giriniz.")
                // Add action buttons
                .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();

        // First get the pulse
        pdLoading = new ProgressDialog(this);
        isProgramfinished=true;
        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this,mHeartRate,SensorManager.SENSOR_DELAY_FASTEST);

        // Show the pulse
        pdLoading.setMessage("\tNabız Ölçülüyor");
        pdLoading.setCancelable(false);
        pdLoading.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String diabetesString = ((EditText)custom_dialog.findViewById(R.id.diabetes)).getText().toString();
                String diastoleString = ((EditText)custom_dialog.findViewById(R.id.diastole)).getText().toString();
                String systoleString = ((EditText)custom_dialog.findViewById(R.id.systole)).getText().toString();
                String pulseString = ((EditText)custom_dialog.findViewById(R.id.pulse)).getText().toString();

                if(!diabetesString.equals("") && !diastoleString.equals("") && !systoleString.equals("") && !pulseString.equals(""))
                {

                    mSensorManager.unregisterListener(activity);

                    float diabetesVal = Float.parseFloat(diabetesString);
                    float diastoleVal = Float.parseFloat(diastoleString);
                    float systoleVal = Float.parseFloat(systoleString);
                    float pulseVal = Float.parseFloat(pulseString);


                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.saved_user_file_key), Context.MODE_PRIVATE);
                    int uid = sharedPref.getInt(getString(R.string.saved_user_uid_key), 0);
                    StatisticsProgramFirebaseDb statisticsProgramFirebaseDb = new StatisticsProgramFirebaseDb(currentPid,String.valueOf(uid),getCurrentDate(),String.valueOf(diastoleVal),String.valueOf(systoleVal),String.valueOf(pulseVal),String.valueOf(diabetesVal),false);
                    mLocalDbHelper.insertStatisticsProgram(statisticsProgramFirebaseDb,mFirebaseDbHelper);
                    Toast.makeText(getApplicationContext(),"Program tamamlandı ve değerleriniz kaydedildi.",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();

                }
                else {
                    Toast.makeText(getApplicationContext(),"Lütfen tüm değerleri giriniz.",Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public static boolean isKitKatWithStepCounter(PackageManager pm) {

        // Require at least Android KitKat
        int currentApiVersion = (int) Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        return currentApiVersion >= 19
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_LOCATION_SETTINGS_REQUEST) {
            checkAndSetupForWalkingExercise();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if(numberOfMeasurement>=6)
        {
            pdLoading.dismiss();
        }


        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCounter++;
            Log.d(TAG,"Step:"+stepCounter);
        }
        else if (sensor.getType() == Sensor.TYPE_HEART_RATE) {
            Log.d(TAG,"Heart:"+value);
            if(value!=-1)
                heartRate.add(String.valueOf(value));
        }
        else if(sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            numberOfStepSensorDetected++;
            if(numberOfStepSensorDetected==1)
                startingStepNumber=value;
            stepCounter=value-startingStepNumber;
            Log.d(TAG,"STEP COUNTER SENSOR CHANGED VALUE:"+value);
        }

        if(isProgramfinished && numberOfMeasurement<6 && value!=0){
            ++numberOfMeasurement;
            heartRateFinal+=value;
            heartRateFinal/=2;
        }
        else if(isProgramfinished && numberOfMeasurement==6){
            //numberOfMeasurement=0;
            numberOfMeasurement++;
            pulseEditText.setText(String.valueOf(heartRateFinal));
            Intent intent = new Intent(this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                    "NABIZ: "+heartRateFinal);
            startActivity(intent);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
