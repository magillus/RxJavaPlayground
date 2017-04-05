package com.example.mat.rxjavaplayground.db;

import android.provider.BaseColumns;

/**
 * Created on 1/17/17.
 */

public final class DatabaseContract {
    private DatabaseContract() {};

    public static final String SQL_CREATE_TABLES =
            "CREATE TABLE "+DataEntry.TABLE_NAME+" (" +
                    DataEntry._ID+ " INTEGER PRIMARY KEY," +
                    DataEntry.COLUMN_NAME_TITLE + " TEXT,"+
                    DataEntry.COLUMN_NAME_DESCRIPTION + " TEXT, "+
                    DataEntry.COLUMN_NAME_Date + " DATE";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;

    public static class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "data_entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_Date = "date";
    }

}
