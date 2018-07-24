package com.muhendis.diabetwatch.helpers;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.muhendis.diabetwatch.db.DiabetWatchDbHelper;
import com.muhendis.diabetwatch.db.Exercise;
import com.muhendis.diabetwatch.db.Program;
import com.muhendis.diabetwatch.db.StatisticsExercise;
import com.muhendis.diabetwatch.db.StatisticsProgram;
import com.muhendis.diabetwatch.model.ProgramExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.ProgramFirebaseDb;
import com.muhendis.diabetwatch.model.StatisticsExerciseFirebaseDb;
import com.muhendis.diabetwatch.model.StatisticsProgramFirebaseDb;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class LocalDBHelper {
    public DiabetWatchDbHelper mDbHelper;
    public SQLiteDatabase mDbReadable, mDbWritable;
    private final String TAG = "LocalDBHelper";
    private Activity activity;

    public LocalDBHelper(DiabetWatchDbHelper mDbHelper, Activity activity) {
        this.mDbHelper = mDbHelper;
        mDbReadable = mDbHelper.getReadableDatabase();
        mDbWritable = mDbHelper.getWritableDatabase();
        this.activity = activity;
    }

    public ProgramExerciseFirebaseDb[] getAllExercisesForProgram(String pid){

        List<ProgramExerciseFirebaseDb> exercises = new ArrayList<ProgramExerciseFirebaseDb>();

        String[] projection = {
                BaseColumns._ID,
                Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP,
                Exercise.ExerciseEntry.COLUMN_NAME_EID,
                Exercise.ExerciseEntry.COLUMN_NAME_HOLD,
                Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION,
                Exercise.ExerciseEntry.COLUMN_NAME_NAME,
                Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_PID,
                Exercise.ExerciseEntry.COLUMN_NAME_REPS,
                Exercise.ExerciseEntry.COLUMN_NAME_SETS,
                Exercise.ExerciseEntry.COLUMN_NAME_UID,
                Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP,
                Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE,
                Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED,
                Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED,
                Exercise.ExerciseEntry.COLUMN_NAME_DURATION
        };

        String selection = Exercise.ExerciseEntry.COLUMN_NAME_PID + " = ?";
        String[] selectionArgs = { pid };

        Cursor cursor = mDbReadable.query(
                Exercise.ExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        while(cursor.moveToNext())
        {
            int dailyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP));
            String eid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_EID));
            int hold = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_HOLD));
            String instruction = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION));
            String exerciseName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_NAME));
            String photoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK));
            String exercisePid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PID));
            int reps = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_REPS));
            String videoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK));
            int sets = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_SETS));
            String uid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_UID));
            int weeklyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP));
            int isWalking = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE));
            int minWalkingSpeed = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED));
            int maxWalkingSpeed = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED));
            int duration = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_DURATION));
            Log.d(TAG,"FETCHED EXERCISE NAME: "+exerciseName +" EPID: "+exercisePid+" pid: "+pid);
            if(isWalking!=0)
            {
                exercises.add(new ProgramExerciseFirebaseDb(dailyRep,eid,hold,exercisePid,reps,sets,uid,weeklyRep,instruction,exerciseName,videoLink,photoLink,true,minWalkingSpeed,maxWalkingSpeed,duration));

            }
            else
                exercises.add(new ProgramExerciseFirebaseDb(dailyRep,eid,hold,exercisePid,reps,sets,uid,weeklyRep,instruction,exerciseName,videoLink,photoLink,false,minWalkingSpeed,maxWalkingSpeed,duration));
        }

        ProgramExerciseFirebaseDb[] allExercisesArray = new ProgramExerciseFirebaseDb[ exercises.size() ];
        exercises.toArray( allExercisesArray );

        return allExercisesArray;
    }

    public ProgramExerciseFirebaseDb[] getAllExercises(){

        List<ProgramExerciseFirebaseDb> exercises = new ArrayList<ProgramExerciseFirebaseDb>();

        String[] projection = {
                BaseColumns._ID,
                Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP,
                Exercise.ExerciseEntry.COLUMN_NAME_EID,
                Exercise.ExerciseEntry.COLUMN_NAME_HOLD,
                Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION,
                Exercise.ExerciseEntry.COLUMN_NAME_NAME,
                Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_PID,
                Exercise.ExerciseEntry.COLUMN_NAME_REPS,
                Exercise.ExerciseEntry.COLUMN_NAME_SETS,
                Exercise.ExerciseEntry.COLUMN_NAME_UID,
                Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP,
                Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE,
                Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED,
                Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED,
                Exercise.ExerciseEntry.COLUMN_NAME_DURATION
        };


        Cursor cursor = mDbReadable.query(
                Exercise.ExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        while(cursor.moveToNext())
        {
            int dailyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP));
            String eid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_EID));
            int hold = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_HOLD));
            String instruction = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION));
            String exerciseName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_NAME));
            String photoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK));
            String exercisePid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PID));
            int reps = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_REPS));
            String videoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK));
            int sets = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_SETS));
            String uid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_UID));
            int weeklyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP));
            int isWalking = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE));
            int minWalkingSpeed = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED));
            int maxWalkingSpeed = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED));
            int duration = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_DURATION));
            if(isWalking!=0)
            {
                exercises.add(new ProgramExerciseFirebaseDb(dailyRep,eid,hold,exercisePid,reps,sets,uid,weeklyRep,instruction,exerciseName,videoLink,photoLink,true,minWalkingSpeed,maxWalkingSpeed,duration));

            }
            else
                exercises.add(new ProgramExerciseFirebaseDb(dailyRep,eid,hold,exercisePid,reps,sets,uid,weeklyRep,instruction,exerciseName,videoLink,photoLink,false,minWalkingSpeed,maxWalkingSpeed,duration));
        }

        ProgramExerciseFirebaseDb[] allExercisesArray = new ProgramExerciseFirebaseDb[ exercises.size() ];
        exercises.toArray( allExercisesArray );

        return allExercisesArray;
    }

    public ProgramFirebaseDb[] getAllPrograms(){

        List<ProgramFirebaseDb> programs = new ArrayList<ProgramFirebaseDb>();

        String[] projection = {
                BaseColumns._ID,
                Program.ProgramEntry.COLUMN_NAME_PID,
                Program.ProgramEntry.COLUMN_NAME_NAME,
                Program.ProgramEntry.COLUMN_NAME_FINISH_DATE,
                Program.ProgramEntry.COLUMN_NAME_UID,
                Program.ProgramEntry.COLUMN_NAME_START_DATE,
                Program.ProgramEntry.COLUMN_NAME_IS_FINISHED,
                Program.ProgramEntry.COLUMN_NAME_DID,
                Program.ProgramEntry.COLUMN_NAME_DIAGNOSIS
        };

        Cursor cursor = mDbReadable.query(
                Program.ProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        while(cursor.moveToNext())
        {
            String programName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_NAME));
            String programPid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_PID));
            String programDiagnosis = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_DIAGNOSIS));
            String programDid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_DID));
            String programFinishDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_FINISH_DATE));
            String programStartDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_START_DATE));
            int programIsFinished = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_IS_FINISHED));
            String programUid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Program.ProgramEntry.COLUMN_NAME_UID));
            programs.add(new ProgramFirebaseDb(programDid,programPid,programUid,!(programIsFinished<1),programDiagnosis,programFinishDate,programName,programStartDate));
        }

        ProgramFirebaseDb[] allProgramsArray = new ProgramFirebaseDb[ programs.size() ];
        programs.toArray( allProgramsArray );

        return allProgramsArray;

    }

    public void insertExercise(ProgramExerciseFirebaseDb exercise){
        if(!checkIfExerciseExistsInLocalDB(exercise.getPid(),exercise.getEid())){
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP, exercise.getDailyRep());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_EID, exercise.getEid());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_HOLD, exercise.getHold());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION, exercise.getInstruction());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_NAME, exercise.getName());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK, exercise.getPhotoLink());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_PID, exercise.getPid());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_REPS, exercise.getReps());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_SETS, exercise.getSets());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_UID, exercise.getUid());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK, exercise.getVideoLink());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP, exercise.getWeeklyRep());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE, exercise.getIsWalking());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED, exercise.getMinWalkingSpeed());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED, exercise.getMaxWalkingSpeed());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_DURATION, exercise.getDuration());



            // Insert the new row, returning the primary key value of the new row
            long newRowId = mDbWritable.insert(Exercise.ExerciseEntry.TABLE_NAME, null, values);

            if(newRowId>0)
            {
                Log.d(TAG,"Exercise inserted: Name: "+exercise.getName());
            }
            else
            {
                Log.d(TAG,"Exercise NOT inserted: ROW ID:"+newRowId+" Name: "+exercise.getName());
            }
        }
        else
        {
            Log.d(TAG,"Exercise NOT inserted: Name: "+exercise.getName());
        }

    }

    public boolean updateExercise(ProgramExerciseFirebaseDb exercise){
        if(checkIfExerciseExistsInLocalDB(exercise.getPid(),exercise.getEid())){
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP, exercise.getDailyRep());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_EID, exercise.getEid());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_HOLD, exercise.getHold());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION, exercise.getInstruction());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_NAME, exercise.getName());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK, exercise.getPhotoLink());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_PID, exercise.getPid());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_REPS, exercise.getReps());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_SETS, exercise.getSets());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_UID, exercise.getUid());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK, exercise.getVideoLink());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP, exercise.getWeeklyRep());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE, exercise.getIsWalking());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED, exercise.getMinWalkingSpeed());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED, exercise.getMaxWalkingSpeed());
            values.put(Exercise.ExerciseEntry.COLUMN_NAME_DURATION, exercise.getDuration());



            // Insert the new row, returning the primary key value of the new row
            long newRowId = mDbWritable.update(Exercise.ExerciseEntry.TABLE_NAME, values,Exercise.ExerciseEntry.COLUMN_NAME_PID + " = ? AND " + Exercise.ExerciseEntry.COLUMN_NAME_EID + " = ?",new String[]{exercise.getPid(),exercise.getEid()});

            if(newRowId>0)
            {
                Log.d(TAG,"Exercise Updated: Name: "+exercise.getName());
            }
            else
            {
                Log.d(TAG,"Exercise NOT updated: ROW ID:"+newRowId+" Name: "+exercise.getName());
            }
        }
        else
        {
            insertExercise(exercise);
            return false;
        }

        return true;

    }

    public void insertStatisticsExercise(StatisticsExerciseFirebaseDb exercise){

        ContentValues values = new ContentValues();
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_EID, exercise.getEid());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE, exercise.getFinishDate());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_PID, exercise.getPid());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_ELAPSED_TIME, exercise.getElapsedTime());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_STEP_COUNTER, exercise.getStepCounter());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_HEART_RATE,UIHelper.convertListToString(exercise.getHeartRate()) );
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_IS_WALKING, exercise.getIsWalking());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKING_SPEEDS, UIHelper.convertListToString(exercise.getWalkingSpeed()));
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKED_DISTANCE, exercise.getWalkedDistance());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = mDbWritable.insert(StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME, null, values);

    }

    public void insertStatisticsExercise(StatisticsExerciseFirebaseDb exercise, FirebaseDBHelper mFirebaseDbHelper){

        ContentValues values = new ContentValues();
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_EID, exercise.getEid());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE, exercise.getFinishDate());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_PID, exercise.getPid());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_ELAPSED_TIME, exercise.getElapsedTime());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_STEP_COUNTER, exercise.getStepCounter());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_HEART_RATE,UIHelper.convertListToString(exercise.getHeartRate()) );
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_IS_WALKING, exercise.getIsWalking());
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKING_SPEEDS, UIHelper.convertListToString(exercise.getWalkingSpeed()));
        values.put(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKED_DISTANCE, exercise.getWalkedDistance());


        // Insert the new row, returning the primary key value of the new row
        long newRowId = mDbWritable.insert(StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME, null, values);
        if(newRowId>0)
            mFirebaseDbHelper.insertStatisticsExercise(exercise, String.valueOf(newRowId));

    }

    public void insertStatisticsProgram(StatisticsProgramFirebaseDb program){

        ContentValues values = new ContentValues();
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PID, program.getPid());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIABETES, program.getDiabetes());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIASTOLE, program.getDiastole());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE, program.getFinishDate());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START, program.getIsBeforeStart());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PULSE, program.getPulse());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_SYSTOLE, program.getSystole());


        // Insert the new row, returning the primary key value of the new row
        long newRowId = mDbWritable.insert(StatisticsProgram.StatisticsProgramEntry.TABLE_NAME, null, values);

    }

    public void insertStatisticsProgram(StatisticsProgramFirebaseDb program, FirebaseDBHelper mFirebaseDbHelper){

        String isBefore="";
        if(program.getIsBeforeStart())
            isBefore="1";
        else
            isBefore="0";

        ContentValues values = new ContentValues();
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PID, program.getPid());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIABETES, program.getDiabetes());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIASTOLE, program.getDiastole());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE, program.getFinishDate());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START, isBefore);
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PULSE, program.getPulse());
        values.put(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_SYSTOLE, program.getSystole());


        // Insert the new row, returning the primary key value of the new row
        long newRowId = mDbWritable.insert(StatisticsProgram.StatisticsProgramEntry.TABLE_NAME, null, values);
        if(newRowId>0)
            mFirebaseDbHelper.insertStatisticsProgram(program, String.valueOf(newRowId));

    }

    public void deleteProgramExercises(String pid){
        // Define 'where' part of query.
        String selection = Exercise.ExerciseEntry.COLUMN_NAME_PID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { pid };
        // Issue SQL statement.
        int deletedRows = mDbReadable.delete(Exercise.ExerciseEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteExercise(String pid, String eid){
        // Define 'where' part of query.
        String selection = Exercise.ExerciseEntry.COLUMN_NAME_PID + " LIKE ? AND " + Exercise.ExerciseEntry.COLUMN_NAME_EID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { pid,eid };
        // Issue SQL statement.
        int deletedRows = mDbReadable.delete(Exercise.ExerciseEntry.TABLE_NAME, selection, selectionArgs);

        File videoFile = new File(activity.getExternalFilesDir(null)
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/Files/videos/"+pid+"_"+eid+".mp4");

        File imageFile = new File(activity.getExternalFilesDir(null)
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/Files/images/"+pid+"_"+eid+".gif");


        if(videoFile.exists())
        {
            Log.d(TAG,"VIDEO FILE REMOVED in local: "+videoFile.getAbsolutePath());
            videoFile.delete();
        }

        if(imageFile.exists()){
            Log.d(TAG,"IMAGE FILE REMOVED in local: "+imageFile.getAbsolutePath());
            imageFile.delete();
        }
    }

    public void deleteProgram(String pid){

        deleteProgramExercises(pid);
        // Define 'where' part of query.
        String selection = Program.ProgramEntry.COLUMN_NAME_PID + " LIKE ? ";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { pid };
        // Issue SQL statement.
        int deletedRows = mDbReadable.delete(Program.ProgramEntry.TABLE_NAME, selection, selectionArgs);

    }


    public boolean checkIfExerciseExistsInLocalDB(String pid, String eid)
    {

        String[] projection = {
                BaseColumns._ID,
                Exercise.ExerciseEntry.COLUMN_NAME_PID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = Exercise.ExerciseEntry.COLUMN_NAME_PID + " = ? AND " + Exercise.ExerciseEntry.COLUMN_NAME_EID + " = ?";
        String[] selectionArgs = { pid,eid };

        Cursor cursor = mDbReadable.query(
                Exercise.ExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        if(cursor.moveToNext())
        {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public ProgramExerciseFirebaseDb getExercise(String pid, String eid)
    {

        String[] projection = {
                BaseColumns._ID,
                Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP,
                Exercise.ExerciseEntry.COLUMN_NAME_EID,
                Exercise.ExerciseEntry.COLUMN_NAME_HOLD,
                Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION,
                Exercise.ExerciseEntry.COLUMN_NAME_NAME,
                Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_PID,
                Exercise.ExerciseEntry.COLUMN_NAME_REPS,
                Exercise.ExerciseEntry.COLUMN_NAME_SETS,
                Exercise.ExerciseEntry.COLUMN_NAME_UID,
                Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP,
                Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE,
                Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED,
                Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED,
                Exercise.ExerciseEntry.COLUMN_NAME_DURATION
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = Exercise.ExerciseEntry.COLUMN_NAME_PID + " = ? AND " + Exercise.ExerciseEntry.COLUMN_NAME_EID + " = ?";
        String[] selectionArgs = { pid,eid };

        Cursor cursor = mDbReadable.query(
                Exercise.ExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        if(cursor.moveToNext())
        {
            String eidLocal = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_EID));
            int dailyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP));
            int hold = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_HOLD));
            String instruction = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION));
            String exerciseName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_NAME));
            String photoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK));
            String exercisePid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PID));
            int reps = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_REPS));
            String videoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK));
            int sets = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_SETS));
            String uid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_UID));
            int weeklyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP));
            int isWalking = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE));
            int minWalkingSpeed = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED));
            int maxWalkingSpeed = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED));
            int duration = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_DURATION));

            cursor.close();

            if(isWalking!=0)
            {
                return new ProgramExerciseFirebaseDb(dailyRep,eidLocal,hold,exercisePid,reps,sets,uid,weeklyRep,instruction,exerciseName,videoLink,photoLink,true,minWalkingSpeed,maxWalkingSpeed,duration);

            }
            else
                return new ProgramExerciseFirebaseDb(dailyRep,eidLocal,hold,exercisePid,reps,sets,uid,weeklyRep,instruction,exerciseName,videoLink,photoLink,false,minWalkingSpeed,maxWalkingSpeed,duration);

        }
        cursor.close();
        return null;
    }

    public boolean isExerciseFinishedToday(String eid, String pid){
        String[] projection = {
                BaseColumns._ID,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_PID + " = ? AND " + StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_EID + " = ? AND " + StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE + " = ? ";
        String[] selectionArgs = { pid,eid,getCurrentDate() };

        Cursor cursor = mDbReadable.query(
                StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        if(cursor.moveToNext())
        {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean isProgramDataEnteredToday(String pid){
        String[] projection = {
                BaseColumns._ID,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PID + " = ? AND " + StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START + " = ? AND " + StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE + " = ? ";
        String[] selectionArgs = { pid,"1", getCurrentDate() };

        Cursor cursor = mDbReadable.query(
                StatisticsProgram.StatisticsProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        if(cursor.moveToNext())
        {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean isAllExerciseFinishedForProgram(String pid)
    {
        int finishedExercise = 0;
        int totalExerciseForProgram = getAllExercisesForProgram(pid).length;


        String[] projection = {
                BaseColumns._ID,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_PID + " = ? AND " + StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE + " = ? ";
        String[] selectionArgs = { pid,getCurrentDate() };

        Cursor cursor = mDbReadable.query(
                StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        while(cursor.moveToNext())
        {
            finishedExercise++;
        }
        Log.d(TAG,"COMPLETED EXERCISE FOR PID "+pid+": "+finishedExercise);
        Log.d(TAG,"TOTAL EXERCISE FOR PID "+pid+": "+totalExerciseForProgram);
        cursor.close();
        return finishedExercise==totalExerciseForProgram;
    }

    public boolean isStatisticsProgramInsertedToday(String pid){
        String[] projection = {
                BaseColumns._ID,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PID + " = ? AND " + StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START + " = ? AND " + StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE + " = ? ";
        String[] selectionArgs = { pid,"0", getCurrentDate() };

        Cursor cursor = mDbReadable.query(
                StatisticsProgram.StatisticsProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        if(cursor.moveToNext())
        {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void deleteAllData(){
        mDbWritable.execSQL("delete from "+ Program.ProgramEntry.TABLE_NAME);
        mDbWritable.execSQL("delete from "+ Exercise.ExerciseEntry.TABLE_NAME);
        mDbWritable.execSQL("delete from "+ StatisticsProgram.StatisticsProgramEntry.TABLE_NAME);
        mDbWritable.execSQL("delete from "+ StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME);
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public StatisticsExerciseFirebaseDb[] getAllStatisticsExercise(String uid){
        List<StatisticsExerciseFirebaseDb> statisticsExerciseFirebaseDbs = new ArrayList<StatisticsExerciseFirebaseDb>();

        String[] projection = {
                BaseColumns._ID,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_EID,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_PID,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_ELAPSED_TIME,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_STEP_COUNTER,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_HEART_RATE,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKING_SPEEDS,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_IS_WALKING,
                StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKED_DISTANCE
        };


        Cursor cursor = mDbReadable.query(
                StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        while(cursor.moveToNext())
        {
            String finishDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE));
            String eid = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_EID));
            String pid = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_PID));
            int elapsedTime = cursor.getInt(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_ELAPSED_TIME));
            int stepCounter = cursor.getInt(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_STEP_COUNTER));
            List<String> heartRate = UIHelper.convertStringToList(cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_HEART_RATE)));
            int isWalking = cursor.getInt(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_IS_WALKING));
            String walkingSpeeds = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKING_SPEEDS));
            int walkedDistance = cursor.getInt(
                    cursor.getColumnIndexOrThrow(StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKED_DISTANCE));

            if(isWalking!=0){
                statisticsExerciseFirebaseDbs.add(new StatisticsExerciseFirebaseDb(finishDate,eid,pid,uid,elapsedTime,stepCounter,heartRate,UIHelper.convertStringToList(walkingSpeeds),true,walkedDistance));

            }
            else
                statisticsExerciseFirebaseDbs.add(new StatisticsExerciseFirebaseDb(finishDate,eid,pid,uid,elapsedTime,stepCounter,heartRate,UIHelper.convertStringToList(walkingSpeeds),false,walkedDistance));


        }

        StatisticsExerciseFirebaseDb[] allExercisesArray = new StatisticsExerciseFirebaseDb[ statisticsExerciseFirebaseDbs.size() ];
        statisticsExerciseFirebaseDbs.toArray( allExercisesArray );

        return allExercisesArray;
    }

    public StatisticsProgramFirebaseDb[] getAllStatisticsProgram(String uid){
        List<StatisticsProgramFirebaseDb> statisticsProgramFirebaseDbs = new ArrayList<StatisticsProgramFirebaseDb>();

        String[] projection = {
                BaseColumns._ID,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PID,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PULSE,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_SYSTOLE,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIASTOLE,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIABETES,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE
        };


        Cursor cursor = mDbReadable.query(
                StatisticsProgram.StatisticsProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        while(cursor.moveToNext())
        {
            int isBeforeStart = cursor.getInt(
                    cursor.getColumnIndexOrThrow(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START));
            String pid = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PID));
            String pulse = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PULSE));
            String systole = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_SYSTOLE));
            String diastole = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIASTOLE));
            String diabetes = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIABETES));
            String finishDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE));
            statisticsProgramFirebaseDbs.add(new StatisticsProgramFirebaseDb(pid,uid,finishDate,diastole,systole,pulse,diabetes,(isBeforeStart==1)));
        }

        StatisticsProgramFirebaseDb[] allProgramsArray = new StatisticsProgramFirebaseDb[ statisticsProgramFirebaseDbs.size() ];
        statisticsProgramFirebaseDbs.toArray( allProgramsArray );

        return allProgramsArray;
    }


    public String[][] getAllLinks(){
        List<String> imageLinks = new ArrayList<String>();
        List<String> videoLinks = new ArrayList<String>();
        List<String> imageFileNames = new ArrayList<String>();
        List<String> videoFileNames = new ArrayList<String>();
        int counterVideo=0,counterImage=0;
        String[][] allLinks = new String[4][];

        String[] projection = {
                BaseColumns._ID,
                Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP,
                Exercise.ExerciseEntry.COLUMN_NAME_EID,
                Exercise.ExerciseEntry.COLUMN_NAME_HOLD,
                Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION,
                Exercise.ExerciseEntry.COLUMN_NAME_NAME,
                Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_PID,
                Exercise.ExerciseEntry.COLUMN_NAME_REPS,
                Exercise.ExerciseEntry.COLUMN_NAME_SETS,
                Exercise.ExerciseEntry.COLUMN_NAME_UID,
                Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK,
                Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP
        };

        Cursor cursor = mDbReadable.query(
                Exercise.ExerciseEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null ,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        while(cursor.moveToNext())
        {
            int dailyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP));
            String eid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_EID));
            int hold = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_HOLD));
            String instruction = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION));
            String exerciseName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_NAME));
            String photoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK));
            String exercisePid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_PID));
            int reps = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_REPS));
            String videoLink = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK));
            int sets = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_SETS));
            String uid = cursor.getString(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_UID));
            int weeklyRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP));


            File videoFile = new File(activity.getExternalFilesDir(null)
                    + "/Android/data/"
                    + activity.getApplicationContext().getPackageName()
                    + "/Files/videos/"+exercisePid+"_"+eid+".mp4");

            File imageFile = new File(activity.getExternalFilesDir(null)
                    + "/Android/data/"
                    + activity.getApplicationContext().getPackageName()
                    + "/Files/images/"+exercisePid+"_"+eid+".gif");

            if (!videoFile.exists())
            {
                Log.d(TAG,"VIDEO FILE DOES NOT EXISTS: "+videoFile.getAbsolutePath());
                videoLinks.add(counterVideo,videoLink);
                videoFileNames.add(counterVideo,exercisePid+"_"+eid+".mp4");
                counterVideo++;
            }


            if (!imageFile.exists()){
                Log.d(TAG,"IMAGE FILE DOES NOT EXISTS: "+imageFile.getAbsolutePath());
                imageLinks.add(counterImage,photoLink);
                imageFileNames.add(counterImage,exercisePid+"_"+eid+".gif");
                counterImage++;
            }
        }
        String[] imageLinksArr = new String[imageLinks.size()];
        imageLinks.toArray(imageLinksArr);

        String[] videoLinksArr = new String[videoLinks.size()];
        videoLinks.toArray(videoLinksArr);

        String[] imageFileNamesArr = new String[imageFileNames.size()];
        imageFileNames.toArray(imageFileNamesArr);

        String[] videoFileNamesArr = new String[videoFileNames.size()];
        videoFileNames.toArray(videoFileNamesArr);

        allLinks[0]=imageLinksArr;
        allLinks[1]=videoLinksArr;
        allLinks[2]=imageFileNamesArr;
        allLinks[3]=videoFileNamesArr;


        return allLinks;
    }

    public boolean isAllProgramFinishedToday(){
        String[] projection = {
                BaseColumns._ID,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START + " = ? AND " + StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE + " = ? ";
        String[] selectionArgs = { "0", getCurrentDate() };

        Cursor cursor = mDbReadable.query(
                StatisticsProgram.StatisticsProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        int programCounter=0;
        while(cursor.moveToNext())
        {
            programCounter++;
        }
        cursor.close();
        return programCounter==getAllPrograms().length;
    }

    public int checkFinishedProgramNumberForLastFiveDays(){
        String[] projection = {
                BaseColumns._ID,
                StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START + " = ? AND " + StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE + " IN(?,?,?,?,?) ";
        String[] selectionArgs = { "0", getCurrentDate(),getDateFor(-1),getDateFor(-2),getDateFor(-3),getDateFor(-4) };

        Cursor cursor = mDbReadable.query(
                StatisticsProgram.StatisticsProgramEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null// don't filter by row groups
        );

        int programCounter=0;
        while(cursor.moveToNext())
        {
            programCounter++;
        }
        cursor.close();
        if(getAllPrograms().length==0)
            return 0;
        else

            return programCounter/getAllPrograms().length;
    }

    public String getDateFor(int numberOfDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, numberOfDays);
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }



}
