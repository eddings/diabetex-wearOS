package com.muhendis.diabetwatch.db;

import android.provider.BaseColumns;

public final class Program {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Program() {}

    /* Inner class that defines the table contents */
    public static class ProgramEntry implements BaseColumns {
        public static final String TABLE_NAME = "program";
        public static final String COLUMN_NAME_DIAGNOSIS = "diagnosis";
        public static final String COLUMN_NAME_DID = "did";
        public static final String COLUMN_NAME_FINISH_DATE = "finishDate";
        public static final String COLUMN_NAME_START_DATE = "startDate";
        public static final String COLUMN_NAME_IS_FINISHED = "isFinished";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_UID = "uid";

    }
}