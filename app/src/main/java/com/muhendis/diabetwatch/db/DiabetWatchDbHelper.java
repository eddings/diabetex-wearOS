package com.muhendis.diabetwatch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiabetWatchDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "DiabetWatch.db";
    private static final String SQL_CREATE_ENTRIES_PROGRAM =
            "CREATE TABLE " + Program.ProgramEntry.TABLE_NAME + " (" +
                    Program.ProgramEntry._ID + " INTEGER PRIMARY KEY," +
                    Program.ProgramEntry.COLUMN_NAME_DIAGNOSIS + " TEXT," +
                    Program.ProgramEntry.COLUMN_NAME_FINISH_DATE + " TEXT," +
                    Program.ProgramEntry.COLUMN_NAME_IS_FINISHED + " INTEGER," +
                    Program.ProgramEntry.COLUMN_NAME_NAME + " TEXT," +
                    Program.ProgramEntry.COLUMN_NAME_START_DATE + " TEXT," +
                    Program.ProgramEntry.COLUMN_NAME_PID + " TEXT," +
                    Program.ProgramEntry.COLUMN_NAME_UID + " TEXT," +
                    Program.ProgramEntry.COLUMN_NAME_DID + " TEXT)";
    private static final String SQL_CREATE_ENTRIES_EXERCISE =
            "CREATE TABLE " + Exercise.ExerciseEntry.TABLE_NAME + " (" +
                    Exercise.ExerciseEntry._ID + " INTEGER PRIMARY KEY," +
                    Exercise.ExerciseEntry.COLUMN_NAME_EID + " TEXT," +
                    Exercise.ExerciseEntry.COLUMN_NAME_DAILY_REP + " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_HOLD + " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_INSTRUCTION + " TEXT," +
                    Exercise.ExerciseEntry.COLUMN_NAME_NAME + " TEXT," +
                    Exercise.ExerciseEntry.COLUMN_NAME_PID + " TEXT," +
                    Exercise.ExerciseEntry.COLUMN_NAME_REPS + " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_UID + " TEXT," +
                    Exercise.ExerciseEntry.COLUMN_NAME_WEEKLY_REP + " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_PHOTO_LINK + " TEXT," +
                    Exercise.ExerciseEntry.COLUMN_NAME_VIDEO_LINK + " TEXT," +
                    Exercise.ExerciseEntry.COLUMN_NAME_IS_WALKING_EXERCISE + " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_MIN_WALKING_SPEED+ " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_MAX_WALKING_SPEED + " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_DURATION+ " INTEGER," +
                    Exercise.ExerciseEntry.COLUMN_NAME_SETS + " INTEGER)";
    private static final String SQL_CREATE_ENTRIES_STATISTICS_EXERCISE =
            "CREATE TABLE " + StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME + " (" +
                    StatisticsExercise.StatisticsExerciseEntry._ID + " INTEGER PRIMARY KEY," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_EID + " TEXT," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_PID + " TEXT," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_ELAPSED_TIME + " INTEGER," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_STEP_COUNTER + " INTEGER," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_HEART_RATE + " TEXT," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKING_SPEEDS + " TEXT," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_IS_WALKING + " INTEGER," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_WALKED_DISTANCE + " INTEGER," +
                    StatisticsExercise.StatisticsExerciseEntry.COLUMN_NAME_FINISH_DATE + " TEXT)";

    private static final String SQL_CREATE_ENTRIES_STATISTICS_PROGRAM =
            "CREATE TABLE " + StatisticsProgram.StatisticsProgramEntry.TABLE_NAME + " (" +
                    StatisticsProgram.StatisticsProgramEntry._ID + " INTEGER PRIMARY KEY," +
                    StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PID + " TEXT," +
                    StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_FINISH_DATE + " TEXT," +
                    StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIABETES + " TEXT," +
                    StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_DIASTOLE + " TEXT," +
                    StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_IS_BEFORE_START + " INTEGER," +
                    StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_SYSTOLE + " TEXT," +
                    StatisticsProgram.StatisticsProgramEntry.COLUMN_NAME_PULSE + " TEXT)";


    private static final String SQL_DELETE_ENTRIES_PROGRAM =
            "DROP TABLE IF EXISTS " + Program.ProgramEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_EXERCISE =
            "DROP TABLE IF EXISTS " + Exercise.ExerciseEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_STATISTICS_EXERCISE =
            "DROP TABLE IF EXISTS " + StatisticsExercise.StatisticsExerciseEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_STATISTICS_PROGRAM =
            "DROP TABLE IF EXISTS " + StatisticsProgram.StatisticsProgramEntry.TABLE_NAME;

    public DiabetWatchDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_PROGRAM);
        db.execSQL(SQL_CREATE_ENTRIES_EXERCISE);
        db.execSQL(SQL_CREATE_ENTRIES_STATISTICS_EXERCISE);
        db.execSQL(SQL_CREATE_ENTRIES_STATISTICS_PROGRAM);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_PROGRAM);
        db.execSQL(SQL_DELETE_ENTRIES_EXERCISE);
        db.execSQL(SQL_DELETE_ENTRIES_STATISTICS_EXERCISE);
        db.execSQL(SQL_DELETE_ENTRIES_STATISTICS_PROGRAM);

        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}