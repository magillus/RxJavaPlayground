package com.example.mat.rxjavaplayground.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.SparseArray;

import com.example.mat.rxjavaplayground.service.RandomString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class TextContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.mat.rxjavaplayground.provider";

    private static final String RANDOM_STRING_PATH = "random";
    private static final String QUERY_PARAM_LIMIT = "limit";

    private static final int CODE_RANDOM_STRING = 100;

    private static final String CONTENT = "content";

    SparseArray<RandomString> randomStringSparseArray = new SparseArray<>();

    private UriMatcher uriMatcher;

    public TextContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, RANDOM_STRING_PATH + "/*", CODE_RANDOM_STRING);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        try {
            RandomString randomString = getRandomString(uri);
            if (randomString != null) {
                randomStringSparseArray.removeAt(randomStringSparseArray.indexOfValue(randomString));
                getContext().getContentResolver().notifyChange(uri, null);
                return 1;
            }
        } catch (Exception ex) {
            Timber.w(ex, "Error processing delete command, uri = %s", uri);
        }
        return 0;
    }

    private int getSizeFromUri(Uri uri) {
        return Integer.valueOf(uri.getLastPathSegment());
    }

    private RandomString getRandomString(Uri uri) {
        return randomStringSparseArray.get(getSizeFromUri(uri));
    }

    @Override
    public String getType(Uri uri) {
        return "text/plain";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try {
            int size = getSizeFromUri(uri);
            if (randomStringSparseArray.get(size) == null) {
                randomStringSparseArray.put(size, new RandomString(size));
            }
            randomStringSparseArray.get(size).nextString();
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        } catch (Exception ex) {
            Timber.w(ex, "Error inserting random string, uri: %s", uri);
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        try {
            RandomString randomString = getRandomString(uri);
            if (randomString != null) {
                MatrixCursor cursor = new MatrixCursor(new String[]{"value"});
                int limit = 1;
                String limitString = uri.getQueryParameter(QUERY_PARAM_LIMIT);
                if (limitString != null) {
                    limit = Integer.valueOf(limitString);
                }
                for (int i = 0; i < limit; i++) {
                    cursor.addRow(new Object[]{randomString.lastString()});
                }
                return cursor;
            }
        } catch (Exception ex) {
            Timber.w(ex, "Error quering for random string uri: %s", uri);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        try {
            RandomString randomString = getRandomString(uri);
            if (randomString != null) {
                randomString.nextString();// skip to next
                return 1;
            } else {
                Timber.i("No update possible, inserting.");
                insert(uri, values);
            }
        } catch (Exception ex) {
            Timber.w(ex, "Error updating random string, uri: %s", uri);
        }
        return 0;
    }


    public static Uri getRandomStringUri(int size, int count) {
        return getRandomStringUri(size)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAM_LIMIT, String.valueOf(count))
                .build();
    }

    public static Uri getRandomStringUri(int size) {
        return new Uri.Builder().authority(AUTHORITY)
                .scheme(CONTENT)
                .appendPath(RANDOM_STRING_PATH)
                .appendPath(String.valueOf(size))
                .build();
    }

    public static List<String> getRandomString(ContentResolver contentResolver, int size, int count) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(getRandomStringUri(size, count), null, null, null, null);
            if (cursor != null) {
                List<String> stringList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    stringList.add(cursor.getString(0));
                }
                return stringList;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return Collections.emptyList();
    }

    public static String getRandomString(ContentResolver contentResolver, int size) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(getRandomStringUri(size), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
