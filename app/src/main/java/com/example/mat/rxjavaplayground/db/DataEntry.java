package com.example.mat.rxjavaplayground.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

/**
 * Created on 1/17/17.
 */

public class DataEntry {
    public String title;
    public String description;
    public Date date;

    public DataEntry() {}
    public DataEntry(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.DataEntry.COLUMN_NAME_TITLE, title);
        contentValues.put(DatabaseContract.DataEntry.COLUMN_NAME_DESCRIPTION, description);
        contentValues.put(DatabaseContract.DataEntry.COLUMN_NAME_Date, date.getTime());
        return contentValues;
    }
    public static DataEntry fromContentValues(ContentValues contentValues) {
        DataEntry  dataEntry = new DataEntry();
        dataEntry.title = contentValues.getAsString(DatabaseContract.DataEntry.COLUMN_NAME_TITLE);
        dataEntry.description = contentValues.getAsString(DatabaseContract.DataEntry.COLUMN_NAME_DESCRIPTION);
        dataEntry.date = new Date(contentValues.getAsLong(DatabaseContract.DataEntry.COLUMN_NAME_Date));
        return dataEntry;
    }

    public static DataEntry fromCursor(Cursor cursor) {
        DataEntry  dataEntry = new DataEntry();
        dataEntry.title = cursor.getString(cursor.getColumnIndex(DatabaseContract.DataEntry.COLUMN_NAME_TITLE));
        dataEntry.description = cursor.getString(cursor.getColumnIndex(DatabaseContract.DataEntry.COLUMN_NAME_DESCRIPTION));
        dataEntry.date = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseContract.DataEntry.COLUMN_NAME_Date)));
        return dataEntry;
    }
}
