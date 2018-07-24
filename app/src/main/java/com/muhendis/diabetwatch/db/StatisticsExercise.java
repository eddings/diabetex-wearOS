package com.muhendis.diabetwatch.db;

import android.provider.BaseColumns;

public final class StatisticsExercise {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private StatisticsExercise() {}

    /* Inner class that defines the table contents */
    public static class StatisticsExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME = "statisticsExercise";
        public static final String COLUMN_NAME_EID = "eid";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_FINISH_DATE = "finishDate";
        public static final String COLUMN_NAME_ELAPSED_TIME = "elapsedTime";
        public static final String COLUMN_NAME_STEP_COUNTER = "stepCounter";
        public static final String COLUMN_NAME_HEART_RATE = "heartRate";
        public static final String COLUMN_NAME_WALKING_SPEEDS = "walkingSpeeds";
        public static final String COLUMN_NAME_IS_WALKING = "isWalking";
        public static final String COLUMN_NAME_WALKED_DISTANCE = "walkedDistance";

    }
}