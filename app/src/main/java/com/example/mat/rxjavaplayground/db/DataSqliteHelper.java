package com.example.mat.rxjavaplayground.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 1/17/17.
 */

public class DataSqliteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "testDb";
    public static final int DATABASE_VERSION = 1;

    public DataSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DatabaseContract.SQL_DELETE);
        onCreate(db);
    }

    public void insertData(List<DataEntry> dataEntries) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (DataEntry dataEntry: dataEntries) {
            db.insert(DatabaseContract.DataEntry.TABLE_NAME, null, dataEntry.toContentValues());
        }
        db.endTransaction();
    }

    public List<DataEntry> fetchAll() {
        List<DataEntry> dataEntries = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(DatabaseContract.DataEntry.TABLE_NAME,
                new String[]{DatabaseContract.DataEntry.COLUMN_NAME_TITLE, DatabaseContract.DataEntry.COLUMN_NAME_DESCRIPTION},
                null, null, null, null, null);

        while (cursor != null && cursor.moveToNext()) {
            dataEntries.add(DataEntry.fromCursor(cursor));
        }

        return dataEntries;
    }
}
