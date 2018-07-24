package com.muhendis.diabetwatch.helpers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;

import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.activities.LoginActivity;
import com.muhendis.diabetwatch.activities.ProgramActivity;
import com.muhendis.diabetwatch.adapters.ProgramWearableAdapter;
import com.muhendis.diabetwatch.db.DiabetWatchDbHelper;
import com.muhendis.diabetwatch.db.Program;
import com.muhendis.diabetwatch.model.ProgramExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.ProgramFirebaseDb;
import com.muhendis.diabetwatch.model.StatisticsExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.StatisticsProgramFirebaseDb;
import com.muhendis.diabetwatch.model.UserFirebaseDb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseDBHelper {
    FirebaseDatabase database;
    DatabaseReference mRef;
    Context context;
    final String TAG="FirebaseDBHelper";
    String token="";
    LocalDBHelper mLocalDBHelper;
    UIHelper mUIHelper;
    Activity activity;


    public FirebaseDBHelper(Context context, Activity activity) {
        this.database = FirebaseDatabase.getInstance();
        this.mRef = this.database.getReference();
        this.context = context;
        this.token = FirebaseInstanceId.getInstance().getToken();
        mLocalDBHelper = new LocalDBHelper(new DiabetWatchDbHelper(context),activity);
        mUIHelper = new UIHelper(activity);
        this.activity = activity;
    }

    public void attemptLogin(String email, String password, final LoginActivity loginActivity){
        final ProgressDialog pdLoading = new ProgressDialog(loginActivity);
        pdLoading.setMessage("\tGiriş Yapılıyor...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        mRef.child("users").orderByChild("email_password").equalTo(email+"_"+password).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    pdLoading.dismiss();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        UserFirebaseDb user = issue.getValue(UserFirebaseDb.class);
                        mRef.child("users/"+user.getPid()+"/token").setValue(FirebaseInstanceId.getInstance().getToken());

                        SharedPreferences sharedPref = loginActivity.getSharedPreferences(loginActivity.getResources().getString(R.string.saved_user_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(loginActivity.getString(R.string.saved_user_isloggedin_key), true);
                        editor.putInt(loginActivity.getString(R.string.saved_user_uid_key), user.getPid());
                        editor.putString(loginActivity.getString(R.string.saved_user_email_key), user.getEmail());
                        editor.putString(loginActivity.getString(R.string.saved_user_name_key), user.getName());
                        editor.putString(loginActivity.getString(R.string.saved_user_surname_key), user.getSurname());

                        editor.apply();
                        syncLocalStatisticsWithFirebase(String.valueOf(user.getPid()));
                        // Before Login delete all data from last user
                        mLocalDBHelper.deleteAllData();
                        //removeAllMediaFiles();
                        syncStatisticsWithFirebase(String.valueOf(user.getPid()));
                        Intent intent  = new Intent(loginActivity,ProgramActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        loginActivity.finish();

                    }
                }
                else{
                    pdLoading.dismiss();
                    mUIHelper.showAlertInvalidUserNameOrPassword();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateFcmMessageRead(String messageId){
        mRef.child("notificationMessages/"+messageId+"/read").setValue(true);
    }


    public void syncDataWithLocalDB(final Activity activity, final ProgramWearableAdapter mProgramAdapter){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.saved_user_file_key), Context.MODE_PRIVATE);
        final int sp_uid = sharedPref.getInt(context.getString(R.string.saved_user_uid_key),0);
        String sp_email = sharedPref.getString(context.getString(R.string.saved_user_email_key),"");
        Log.d(TAG,"SPUID:"+sp_uid);

        final DiabetWatchDbHelper mDbHelper = new DiabetWatchDbHelper(context);

        mRef.child("programs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"PROGRAM VALUE EVENT ON DATA CHANGE");

                if(dataSnapshot.exists())
                {
                    Log.d(TAG,"PROGRAM VALUE EVENT ON DATA CHANGE DATA SNAPHOT EXISTS");

                    mRef.child("programs").orderByChild("uid").equalTo(String.valueOf(sp_uid)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG,"PROGRAMS ORDERED VALUE EVENT ON DATA CHANGE");

                            if(dataSnapshot.exists())
                            {
                                Log.d(TAG,"DATA SNAPHOT EXISTS");
                                int firebaseProgramCounter=0;
                                List<String> programIds = new ArrayList<String>();
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    firebaseProgramCounter++;
                                    ProgramFirebaseDb program = issue.getValue(ProgramFirebaseDb.class);
                                    programIds.add(program.getPid());

                                    if(!checkIfProgramExistsInLocalDB(program.getPid(),mDbHelper)){
                                        insertProgramToLocalDB(program,mDbHelper);
                                    }
                                    else{
                                        updateProgramInLocalDB(program,mDbHelper);
                                    }
                                }
                                // Convert id list to array
                                String[] firebaseProgramIds = new String[ programIds.size() ];
                                programIds.toArray( firebaseProgramIds );
                                syncLocalDBProgramsWithFirebase(firebaseProgramCounter,mDbHelper, firebaseProgramIds,mProgramAdapter,activity);
                                mUIHelper.syncMediaFiles(mLocalDBHelper,activity);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void syncStatisticsWithFirebase(String sp_uid){
        mRef.child("statisticsExercise").orderByChild("uid").equalTo(sp_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        StatisticsExerciseFirebaseDb statisticsExerciseFirebaseDb = issue.getValue(StatisticsExerciseFirebaseDb.class);
                        mLocalDBHelper.insertStatisticsExercise(statisticsExerciseFirebaseDb);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("statisticsProgram").orderByChild("uid").equalTo(sp_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        StatisticsProgramFirebaseDb statisticsProgramFirebaseDb = issue.getValue(StatisticsProgramFirebaseDb.class);
                        mLocalDBHelper.insertStatisticsProgram(statisticsProgramFirebaseDb);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean checkIfProgramExistsInLocalDB(String pid, DiabetWatchDbHelper mDbHelper)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                Program.ProgramEntry.COLUMN_NAME_PID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = Program.ProgramEntry.COLUMN_NAME_PID + " = ?";
        String[] selectionArgs = { pid };

        Cursor cursor = db.query(
                Program.ProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        if(cursor.moveToNext())
        {
            Log.d(TAG,"EXISTS IN LOCAL DB Program name: "+pid);
            cursor.close();
            return true;
        }
        Log.d(TAG,"NOT EXISTS IN LOCAL DB Program name: "+pid);
        cursor.close();
        return false;
    }

    public boolean insertProgramToLocalDB(ProgramFirebaseDb program, DiabetWatchDbHelper mDbHelper){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Program.ProgramEntry.COLUMN_NAME_NAME, program.getName());
        values.put(Program.ProgramEntry.COLUMN_NAME_DIAGNOSIS, program.getDiagnosis());
        values.put(Program.ProgramEntry.COLUMN_NAME_DID, program.getDid());
        values.put(Program.ProgramEntry.COLUMN_NAME_FINISH_DATE, program.getFinishDate());
        values.put(Program.ProgramEntry.COLUMN_NAME_IS_FINISHED, program.isFinished());
        values.put(Program.ProgramEntry.COLUMN_NAME_PID, program.getPid());
        values.put(Program.ProgramEntry.COLUMN_NAME_START_DATE, program.getStartDate());
        values.put(Program.ProgramEntry.COLUMN_NAME_UID, program.getUid());


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Program.ProgramEntry.TABLE_NAME, null, values);
        if(newRowId==-1)
        {
            Log.d(TAG,"NOT INSERTED Program name: "+program.getName());
            return false;
        }
        else
        {
            Log.d(TAG,"INSERTED Program name: "+program.getName() +" pid: "+program.getPid());
            setLinksDownloaded(false);

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.logo);
            // Create a notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setLargeIcon(icon)
                            //.setBadgeIconType(R.drawable.logo)
                            .setSmallIcon(R.drawable.logo_white)
                            .setContentTitle(program.getName()+" adlı yeni programınız bulunmaktadır.")
                            .setContentText("Yeni Program");

            Intent resultIntent = new Intent(activity, ProgramActivity.class);

            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            // Gets an instance of the NotificationManager service//

            NotificationManagerCompat mNotificationManager =

                    NotificationManagerCompat.from(activity);

            // When you issue multiple notifications about the same type of event,
            // it’s best practice for your app to try to update an existing notification
            // with this new information, rather than immediately creating a new notification.
            // If you want to update this notification at a later date, you need to assign it an ID.
            // You can then use this ID whenever you issue a subsequent notification.
            // If the previous notification is still visible, the system will update this existing notification,
            // rather than create a new one. In this example, the notification’s ID is 001//

            mNotificationManager.notify(001, mBuilder.build());

            mRef.child("programExercises").orderByChild("uid_pid").equalTo(program.getUid()+"_"+program.getPid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for (DataSnapshot issue :
                                dataSnapshot.getChildren()) {
                            ProgramExerciseFirebaseDb exercise = issue.getValue(ProgramExerciseFirebaseDb.class);
                            mLocalDBHelper.insertExercise(exercise);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return true;

        }
    }

    public boolean updateProgramInLocalDB(final ProgramFirebaseDb program, DiabetWatchDbHelper mDbHelper){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Program.ProgramEntry.COLUMN_NAME_NAME, program.getName());
        values.put(Program.ProgramEntry.COLUMN_NAME_DIAGNOSIS, program.getDiagnosis());
        values.put(Program.ProgramEntry.COLUMN_NAME_DID, program.getDid());
        values.put(Program.ProgramEntry.COLUMN_NAME_FINISH_DATE, program.getFinishDate());
        values.put(Program.ProgramEntry.COLUMN_NAME_IS_FINISHED, program.isFinished());
        values.put(Program.ProgramEntry.COLUMN_NAME_PID, program.getPid());
        values.put(Program.ProgramEntry.COLUMN_NAME_START_DATE, program.getStartDate());
        values.put(Program.ProgramEntry.COLUMN_NAME_UID, program.getUid());


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.update(Program.ProgramEntry.TABLE_NAME, values, Program.ProgramEntry.COLUMN_NAME_PID+" = ?", new String[]{program.getPid()});
        if(newRowId==-1)
        {
            Log.d(TAG,"NOT UPDATED Program name: "+program.getName());
            return false;
        }
        else
        {
            Log.d(TAG,"UPDATED Program name: "+program.getName() +" pid: "+program.getPid());

            final String pid = program.getPid();

            mRef.child("programExercises").orderByChild("uid_pid").equalTo(program.getUid()+"_"+program.getPid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        int firebaseExerciseCounter=0;
                        List<String> exerciseIds = new ArrayList<String>();
                        boolean isUpdated = true;

                        for (DataSnapshot issue :
                                dataSnapshot.getChildren()) {
                            ProgramExerciseFirebaseDb exercise = issue.getValue(ProgramExerciseFirebaseDb.class);
                            isUpdated &= mLocalDBHelper.updateExercise(exercise);
                            firebaseExerciseCounter++;
                            //exerciseIds.add(exercise.getPid()+"_"+exercise.getEid());
                            exerciseIds.add(exercise.getPid()+"_"+exercise.getEid());
                        }

                        syncLocalDBExercisesWithFirebase(firebaseExerciseCounter,exerciseIds,pid);
                        if(!isUpdated)
                            setLinksDownloaded(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return true;

        }
    }

    /*
     * This method checks whether the number of programs is equal to firebase db or not
     * if it is not equal remove the additional programs(probably a program is removed from firebase db but not from
     * local db)
     * */
    public void syncLocalDBExercisesWithFirebase(int firebaseExerciseCounter, List<String> eid_pid, String pid){
        //ProgramExerciseFirebaseDb[] allExercises = mLocalDBHelper.getAllExercises();
        ProgramExerciseFirebaseDb[] allExercises = mLocalDBHelper.getAllExercisesForProgram(pid);
        int deletedFromLocalDb=0;

        for (ProgramExerciseFirebaseDb pe :
                allExercises) {
            if(!eid_pid.contains(pe.getPid()+"_"+pe.getEid())){
                mLocalDBHelper.deleteExercise(pe.getPid(),pe.getEid());
                deletedFromLocalDb++;

            }
        }

    }

    /*
    * This method checks whether the number of programs is equal to firebase db or not
    * if it is not equal remove the additional programs(probably a program is removed from firebase db but not from
    * local db)
    * */
    public void syncLocalDBProgramsWithFirebase(int firebaseDBProgramCounter, DiabetWatchDbHelper mDbHelper, String[] programIds, ProgramWearableAdapter mProgramAdapter, Activity activity){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                Program.ProgramEntry.COLUMN_NAME_PID
        };

        Cursor cursor = db.query(
                Program.ProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        int programCounter=0;

        while(cursor.moveToNext())
        {
            programCounter++;
        }

        if(firebaseDBProgramCounter<programCounter && cursor.moveToFirst()){
            String itemPid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_PID));

            if(!Arrays.asList(programIds).contains(itemPid)){
                // Define 'where' part of query.
                String selection = Program.ProgramEntry.COLUMN_NAME_PID + " LIKE ?";
                // Specify arguments in placeholder order.
                String[] selectionArgs = { itemPid };
                // Issue SQL statement.
                int deletedRows = db.delete(Program.ProgramEntry.TABLE_NAME, selection, selectionArgs);

                removeAllMediaFilesForProgram(itemPid);

                mLocalDBHelper.deleteProgramExercises(itemPid);

            }
            while(cursor.moveToNext()){
                itemPid = cursor.getString(
                        cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_PID));
                Log.d(TAG,"Item Pid from syncLocalDBProgramsWithFirebase: "+itemPid);
                if(!Arrays.asList(programIds).contains(itemPid)){
                    // Define 'where' part of query.
                    String selection = Program.ProgramEntry.COLUMN_NAME_PID + " LIKE ?";
                    // Specify arguments in placeholder order.
                    String[] selectionArgs = { itemPid };
                    // Issue SQL statement.
                    int deletedRows = db.delete(Program.ProgramEntry.TABLE_NAME, selection, selectionArgs);

                    removeAllMediaFilesForProgram(itemPid);

                    mLocalDBHelper.deleteProgramExercises(itemPid);

                }
            }
        }
        cursor.close();
        ProgramFirebaseDb[] ap = mLocalDBHelper.getAllPrograms();
        if (ap.length != 0) {
            activity.findViewById(R.id.noProgramFrameLayout).setVisibility(View.GONE);
        }
        else{
            activity.findViewById(R.id.noProgramFrameLayout).setVisibility(View.VISIBLE);
        }
        mProgramAdapter.setAdapter(ap);
    }

    public void insertStatisticsExercise(StatisticsExerciseFirebaseDb mStatisticsExercise,String localDbId)
    {
        mRef.child("statisticsExercise/"+mStatisticsExercise.getUid()+"_"+mStatisticsExercise.getPid()+"_"+mStatisticsExercise.getEid()+"_"+mStatisticsExercise.getFinishDate()).setValue(mStatisticsExercise);
    }

    public void insertStatisticsProgram(StatisticsProgramFirebaseDb mStatisticsProgram, String localDbId)
    {
        String isBefore = "0";
        if(mStatisticsProgram.getIsBeforeStart())
            isBefore="1";
        mRef.child("statisticsProgram/"+mStatisticsProgram.getUid()+"_"+mStatisticsProgram.getPid()+"_"+isBefore+"_"+mStatisticsProgram.getFinishDate()).setValue(mStatisticsProgram);
    }

    /*
    * This method makes sure of statistics are inserted to firebase in case of connection lost etc.
    * */
    public void syncLocalStatisticsWithFirebase(String userId){
        final String uid = userId;
        mRef.child("statisticsExercise").orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    int firebaseStatisticsExerciseCounter= 0;
                    for (DataSnapshot issue :
                            dataSnapshot.getChildren()) {
                        firebaseStatisticsExerciseCounter++;
                    }
                    StatisticsExerciseFirebaseDb[] allStatisticsExerciseLocalDb = mLocalDBHelper.getAllStatisticsExercise(uid);
                    //if(firebaseStatisticsExerciseCounter<allStatisticsExerciseLocalDb.length)
                    //{
                        for (StatisticsExerciseFirebaseDb se :
                                allStatisticsExerciseLocalDb) {
                            insertStatisticsExercise(se,"");
                        }
                    //}
                }
                else{
                    int firebaseStatisticsExerciseCounter= 0;

                    StatisticsExerciseFirebaseDb[] allStatisticsExerciseLocalDb = mLocalDBHelper.getAllStatisticsExercise(uid);
                    //if(firebaseStatisticsExerciseCounter<allStatisticsExerciseLocalDb.length)
                    //{
                        for (StatisticsExerciseFirebaseDb se :
                                allStatisticsExerciseLocalDb) {
                            insertStatisticsExercise(se,"");
                        }
                    //}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("statisticsProgram").orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    int firebaseStatisticsProgramCounter= 0;
                    for (DataSnapshot issue :
                            dataSnapshot.getChildren()) {
                        firebaseStatisticsProgramCounter++;
                    }
                    StatisticsProgramFirebaseDb[] allStatisticsProgramLocalDb = mLocalDBHelper.getAllStatisticsProgram(uid);
                    //if(firebaseStatisticsProgramCounter<allStatisticsProgramLocalDb.length)
                    //{
                        for (StatisticsProgramFirebaseDb sp :
                                allStatisticsProgramLocalDb) {
                            insertStatisticsProgram(sp,"");
                        }
                    //}
                }
                else{
                    int firebaseStatisticsProgramCounter= 0;

                    StatisticsProgramFirebaseDb[] allStatisticsProgramLocalDb = mLocalDBHelper.getAllStatisticsProgram(uid);
                    //if(firebaseStatisticsProgramCounter<allStatisticsProgramLocalDb.length)
                    //{
                        for (StatisticsProgramFirebaseDb sp :
                                allStatisticsProgramLocalDb) {
                            insertStatisticsProgram(sp,"");
                        }
                    //}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setLinksDownloaded(boolean isDownloaded){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.saved_user_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.saved_user_all_videos_downloaded_key), isDownloaded);
        editor.apply();
    }

    public void removeAllMediaFilesForProgram(String itemPid){
        ProgramExerciseFirebaseDb[] exercisesForProgram = mLocalDBHelper.getAllExercisesForProgram(itemPid);
        // First remove video and image files from sttorage then remove exercises from db
        for(ProgramExerciseFirebaseDb pe : exercisesForProgram){
            File videoFile = new File(activity.getExternalFilesDir(null)
                    + "/Android/data/"
                    + activity.getApplicationContext().getPackageName()
                    + "/Files/videos/"+pe.getPid()+"_"+pe.getEid()+".mp4");

            File imageFile = new File(activity.getExternalFilesDir(null)
                    + "/Android/data/"
                    + activity.getApplicationContext().getPackageName()
                    + "/Files/images/"+pe.getPid()+"_"+pe.getEid()+".gif");

            if(videoFile.exists())
            {
                Log.d(TAG,"VIDEO FILE REMOVED: "+videoFile.getAbsolutePath());
                videoFile.delete();
            }


            if(imageFile.exists())
            {
                Log.d(TAG,"IMAGE FILE REMOVED: "+imageFile.getAbsolutePath());
                imageFile.delete();
            }
        }
    }
    
    public void removeCompletedPrograms(List<String> pidsToRemove){
        for (String pid :
                pidsToRemove) {
            mRef.child("programs").child(pid).removeValue();
        }
    }

    public void removeAllMediaFiles(){


        File videoDir = new File(activity.getExternalFilesDir(null)
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/Files/videos");

        File imageDir = new File(activity.getExternalFilesDir(null)
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/Files/images");

        if(imageDir.exists())
        {
            String[] children = imageDir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(imageDir, children[i]).delete();
            }
        }


        if(videoDir.exists())
        {
            String[] children = videoDir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(videoDir, children[i]).delete();
            }
        }

    }

}
