package com.muhendis.diabetwatch.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.adapters.ExerciseWearableAdapter;
import com.muhendis.diabetwatch.callbacks.CustomScrollingLayoutCallback;
import com.muhendis.diabetwatch.db.DiabetWatchDbHelper;
import com.muhendis.diabetwatch.helpers.FirebaseDBHelper;
import com.muhendis.diabetwatch.helpers.LocalDBHelper;
import com.muhendis.diabetwatch.helpers.UIHelper;
import com.muhendis.diabetwatch.model.ProgramExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.StatisticsProgramFirebaseDb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExerciseActivity extends WearableActivity implements SensorEventListener{
    private String pid;
    private WearableRecyclerView mWearableRecyclerView;
    private DiabetWatchDbHelper mDbHelper;
    private ProgramExerciseFirebaseDb[] mAllPrograms;
    private ExerciseWearableAdapter adapter;
    private LocalDBHelper mLocalDBHelper;
    private UIHelper mUIHelper;
    private FirebaseDBHelper mFirebaseDbHelper;
    private final int diabetesMin=80,diabetesMax=400,systoleMin=90,systoleMax=180,diastoleMin=50,diastoleMax=150,pulseMin=50,pulseMax=145;
    private int heartRate=0,numberOfMeasurement=0;
    private ProgressDialog pdLoading;
    private EditText pulseEditText;

    SensorManager mSensorManager;
    Sensor mHeartRateSensor;

    private final String TAG = "ExercisesActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);



        mFirebaseDbHelper = new FirebaseDBHelper(getApplicationContext(),this);

        // Enables Always-on
        setAmbientEnabled();

        Intent intent = getIntent();
        pid = intent.getStringExtra(getResources().getString(R.string.intent_key_pid));

        mDbHelper = new DiabetWatchDbHelper(getApplicationContext());
        mLocalDBHelper = new LocalDBHelper(mDbHelper,this);
        mUIHelper = new UIHelper(this);

        setupWearableRecylerView();

        if(mLocalDBHelper.isAllExerciseFinishedForProgram(pid)
                && !mLocalDBHelper.isStatisticsProgramInsertedToday(pid))
        {
            showProgramFinished(mLocalDBHelper,pid);
        }
        else if(!mLocalDBHelper.isAllExerciseFinishedForProgram(pid))
        {
            checkIfProgramStartedToday();
        }
        mUIHelper.syncMediaFiles(mLocalDBHelper,this);

    }

    public void setupWearableRecylerView(){
        mAllPrograms = mLocalDBHelper.getAllExercisesForProgram(pid);

        mWearableRecyclerView = findViewById(R.id.exercise_recycler_view);

        adapter = new ExerciseWearableAdapter(mAllPrograms,this);
        adapter.setLocalDbHelper(mLocalDBHelper);

        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();

        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        mWearableRecyclerView.setItemViewCacheSize(30);
        mWearableRecyclerView.setAdapter(adapter);

    }

    public void checkIfProgramStartedToday(){
        if(!mLocalDBHelper.isProgramDataEnteredToday(pid))
        {


            // Then show the dilog to get diabetes,systole and diastole
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppThemeSwipeNoDismiss));
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();
            final View custom_dialog = inflater.inflate(R.layout.custom_dialog, null);
            pulseEditText = custom_dialog.findViewById(R.id.pulse);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(custom_dialog)
                    .setCancelable(false)
                    .setTitle("Lütfen egzersizlere başlamadan istenilen değerleri giriniz")
                    .setNegativeButton("VAZGEÇ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
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
            mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
            mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
                        float diabetesVal = Float.parseFloat(diabetesString);
                        float diastoleVal = Float.parseFloat(diastoleString);
                        float systoleVal = Float.parseFloat(systoleString);
                        float pulseVal = Float.parseFloat(pulseString);

                        Log.d(TAG,"diabetes:"+diabetesMax+" diastole:"+diastoleMax+" systole:"+systoleMax+" pulse:"+pulseMax);
                        if(diabetesVal<diabetesMin || diabetesVal>diabetesMax ||
                                diastoleVal<diastoleMin || diastoleVal>diastoleMax ||
                                systoleVal<systoleMin || systoleVal>systoleMax ||
                                pulseVal<pulseMin || pulseVal>pulseMax)
                        {
                            Toast.makeText(getApplicationContext(),"Değerleriniz egzersiz yapmak için uygun değil. Sonra tekrar deneyiniz.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.saved_user_file_key), Context.MODE_PRIVATE);
                            int uid = sharedPref.getInt(getString(R.string.saved_user_uid_key), 0);
                            StatisticsProgramFirebaseDb statisticsProgramFirebaseDb = new StatisticsProgramFirebaseDb(pid,String.valueOf(uid),getCurrentDate(),String.valueOf(diastoleVal),String.valueOf(systoleVal),String.valueOf(pulseVal),String.valueOf(diabetesVal),true);
                            mLocalDBHelper.insertStatisticsProgram(statisticsProgramFirebaseDb,mFirebaseDbHelper);
                            Toast.makeText(getApplicationContext(),"Değerleriniz kaydedildi. Egzersizlerinize başlayabilirsiniz.",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Lütfen tüm değerleri giriniz.",Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public void showProgramFinished(LocalDBHelper mLocalDBHelper, String pid){
        final LocalDBHelper mLocalDbHelper = mLocalDBHelper;
        final String currentPid = pid;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        final View custom_dialog = inflater.inflate(R.layout.custom_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        pulseEditText = custom_dialog.findViewById(R.id.pulse);


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
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
        {
            setResult(Activity.RESULT_OK);
            finish();
        }
        else if(resultCode == Keys.FINISHED_EXERCISE_RESULT_CODE)
        {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            mSensorManager.unregisterListener(this);
        }
        catch (Exception e){

        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            if(numberOfMeasurement>=6)
            {
                pdLoading.dismiss();
                mSensorManager.unregisterListener(this);
            }
            // Save the heart rate only when it is not equal to 0
            if((int)sensorEvent.values[0]!=0 && numberOfMeasurement<6){
                ++numberOfMeasurement;
                heartRate+=(int)sensorEvent.values[0];
                heartRate/=2;
                String msg = "Ortalama Kalp: " + heartRate+" Number of measurement="+(numberOfMeasurement);
                Log.d(TAG, msg);
            }
            else if(numberOfMeasurement==6){
                pulseEditText.setText(String.valueOf(heartRate));
                Intent intent = new Intent(this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                        "NABIZ: "+heartRate);
                startActivity(intent);
            }

        }
        else
            Log.d(TAG, "Unknown sensor type");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
