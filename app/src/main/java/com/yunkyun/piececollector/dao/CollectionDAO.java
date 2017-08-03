package com.yunkyun.piececollector.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yunkyun.piececollector.object.Collection;

/**
 * Created by YunKyun on 2017-08-03.
 */

public class CollectionDAO extends SQLiteOpenHelper {
    private static final String TAG = "PlaceDAO";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "piece_collector";
    // Table name
    private static final String TABLE_NAME = "collections";
    // Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_PATH = "image_path";

    public CollectionDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IMAGE_PATH + " TEXT" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*List<Collection> selectPlaces() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<Collection> tupleList = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    Place tuple = new Place();
                    tuple.setId(cursor.getInt(0));
                    tuple.setTitle(cursor.getString(1));
                    tuple.setLatitude(cursor.getDouble(2));
                    tuple.setLongitude(cursor.getDouble(3));
                    tuple.setAreaCode(cursor.getInt(4));
                    tuple.setLocalCode(cursor.getInt(5));
                    tuple.setImagePath(cursor.getString(6));
                    tuple.setType(cursor.getInt(7));
                    tuple.setCat1(cursor.getString(8));
                    tuple.setCat2(cursor.getString(9));
                    tuple.setCat3(cursor.getString(10));
                    tuple.setCollectionID(cursor.getInt(11));
                    tuple.setVisited(cursor.getInt(12));

                    tupleList.add(tuple);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            db.close();
        }

        return tupleList;
    }*/

    boolean insertCollection(Collection tuple) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, tuple.getId());
        values.put(KEY_TITLE, tuple.getTitle());
        values.put(KEY_DESCRIPTION, tuple.getDescription());
        values.put(KEY_IMAGE_PATH, tuple.getImagePath());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id != -1;
    }
}
