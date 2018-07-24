package com.muhendis.diabetwatch.db;

import android.provider.BaseColumns;

public final class Exercise {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Exercise() {}

    /* Inner class that defines the table contents */
    public static class ExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_NAME_DAILY_REP = "dailyRep";
        public static final String COLUMN_NAME_WEEKLY_REP = "weeklyRep";
        public static final String COLUMN_NAME_EID = "eid";
        public static final String COLUMN_NAME_HOLD = "hold";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_REPS = "reps";
        public static final String COLUMN_NAME_SETS = "sets";
        public static final String COLUMN_NAME_UID = "uid";
        public static final String COLUMN_NAME_INSTRUCTION = "instruction";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_VIDEO_LINK = "videoLink";
        public static final String COLUMN_NAME_PHOTO_LINK = "photoLink";
        public static final String COLUMN_NAME_IS_WALKING_EXERCISE = "isWalkingExercise";
        public static final String COLUMN_NAME_MIN_WALKING_SPEED = "minWalkingSpeed";
        public static final String COLUMN_NAME_MAX_WALKING_SPEED = "maxWalkingSpeed";
        public static final String COLUMN_NAME_DURATION = "duration";

    }
}