package com.yunkyun.piececollector.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.yunkyun.piececollector.object.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YunKyun on 2017-08-02.
 */

public class PlaceDAO extends SQLiteOpenHelper {
    private static final String TAG = "PlaceDAO";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "piece_collector";
    // Table name
    private static final String TABLE_NAME = "places";
    // Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_AREA_CODE = "area_code";
    private static final String KEY_LOCAL_CODE = "local_Code";
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_TYPE = "type";
    private static final String KEY_CAT1 = "cat1";
    private static final String KEY_CAT2 = "cat2";
    private static final String KEY_CAT3 = "cat3";
    private static final String KEY_COLLECTION_ID = "collection_id";
    private static final String KEY_VISITED = "visited";

    public PlaceDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL,"
                + KEY_AREA_CODE + " INTEGER,"
                + KEY_LOCAL_CODE + " INTEGER,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_TYPE + " INTEGER,"
                + KEY_CAT1 + " TEXT,"
                + KEY_CAT2 + " TEXT,"
                + KEY_CAT3 + " TEXT,"
                + KEY_COLLECTION_ID + " INTEGER,"
                + KEY_VISITED + " INTEGER" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void updatePlaceById(long id) {
        String query = String.format("UPDATE %s SET %s = '1' WHERE %s = '%f'", TABLE_NAME, KEY_VISITED, KEY_ID, id);
        //String query = "UPDATE " + TABLE_NAME + " SET " + KEY_VISITED + " = '1'" + " WHERE " + KEY_ID + " = '" + id + "'";
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            db.execSQL(query);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            db.close();
        }
    }

    public Place selectPlaceById(long id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + KEY_ID + " = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Place tuple = new Place();
        try {
            if (cursor.moveToFirst()) {
                do {
                    tuple.setId(cursor.getLong(0));
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
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            db.close();
        }

        return tuple;
    }

    public List<Place> selectPlacesByLatLng(LatLng farRight, LatLng nearLeft) {
        double maxLatitude = farRight.latitude;
        double maxLongitude = farRight.longitude;
        double minLatitude = nearLeft.latitude;
        double minLongitude = nearLeft.longitude;
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + KEY_LATITUDE + " <= '" + maxLatitude + "' AND "
                + KEY_LATITUDE + " >= '" + minLatitude + "' AND "
                + KEY_LONGITUDE + " <= '" + maxLongitude + "' AND "
                + KEY_LONGITUDE + " >= '" + minLongitude + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<Place> tupleList = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    Place tuple = new Place();
                    tuple.setId(cursor.getLong(0));
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
    }

    public void insertPlaces(List<Place> placeList) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            for (Place tuple : placeList) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, tuple.getId());
                values.put(KEY_TITLE, tuple.getTitle());
                values.put(KEY_LATITUDE, tuple.getLatitude());
                values.put(KEY_LONGITUDE, tuple.getLongitude());
                values.put(KEY_AREA_CODE, tuple.getAreaCode());
                values.put(KEY_LOCAL_CODE, tuple.getLocalCode());
                values.put(KEY_IMAGE_PATH, tuple.getImagePath());
                values.put(KEY_TYPE, tuple.getType());
                values.put(KEY_CAT1, tuple.getCat1());
                values.put(KEY_CAT2, tuple.getCat2());
                values.put(KEY_CAT3, tuple.getCat3());
                values.put(KEY_COLLECTION_ID, tuple.getCollectionID());
                values.put(KEY_VISITED, tuple.getVisited());

                db.insert(TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            db.close();
        }
    }
}
