package com.muhendis.diabetwatch.db;

import android.provider.BaseColumns;

public final class StatisticsProgram {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private StatisticsProgram() {}

    /* Inner class that defines the table contents */
    public static class StatisticsProgramEntry implements BaseColumns {
        public static final String TABLE_NAME = "statisticsProgram";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_IS_BEFORE_START = "isBeforeStart";
        public static final String COLUMN_NAME_FINISH_DATE = "finishDate";
        public static final String COLUMN_NAME_DIASTOLE  = "diastole";
        public static final String COLUMN_NAME_SYSTOLE = "systole";
        public static final String COLUMN_NAME_PULSE = "pulse";
        public static final String COLUMN_NAME_DIABETES = "diabetes";

    }
}