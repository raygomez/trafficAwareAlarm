package com.silex.ragomez.trafficawarealarm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();
    private static DatabaseHandler instance;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "alarmManager.db";
    private static final String TABLE_ALARMS = "alarms";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";

    public static final String KEY_ORIGIN = "origin";
    public static final String KEY_ORIGIN_LAT = "origin_lat";
    public static final String KEY_ORIGIN_LONG = "origin_long";

    public static final String KEY_DEST = "destination";
    public static final String KEY_DEST_LAT = "dest_lat";
    public static final String KEY_DEST_LONG = "dest_long";

    public static final String KEY_PREP_TIME = "prep_time";
    public static final String KEY_DEFAULT_ALARM = "default_alarm";

    public static final String KEY_ETA = "eta";

    public static final String KEY_STATUS = "status";

    public static synchronized DatabaseHandler getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ALARM_TABLE = "create table " + TABLE_ALARMS +
                "(" +
                KEY_ID + " integer primary key autoincrement," +
                KEY_NAME + " text not null," +
                KEY_ORIGIN + " text not null," +
                KEY_ORIGIN_LAT + " real not null," +
                KEY_ORIGIN_LONG + " real not null," +
                KEY_DEST + " text not null," +
                KEY_DEST_LAT + " real not null," +
                KEY_DEST_LONG + " real not null," +
                KEY_PREP_TIME + " integer not null," +
                KEY_DEFAULT_ALARM + " integer not null," +
                KEY_ETA + " integer not null," +
                KEY_STATUS + " integer not null" +
                ")";

        db.execSQL(CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_ALARMS);
        onCreate(db);
    }

    public long addAlarm(Alarm alarm) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = getContentValues(alarm);
        return db.insertOrThrow(TABLE_ALARMS, null, values);
    }

    @NonNull
    private ContentValues getContentValues(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alarm.getName());
        values.put(KEY_ORIGIN, alarm.getOrigin());
        values.put(KEY_ORIGIN_LAT, alarm.getOriginCoordinates().latitude);
        values.put(KEY_ORIGIN_LONG, alarm.getOriginCoordinates().longitude);
        values.put(KEY_DEST, alarm.getDestination());
        values.put(KEY_DEST_LAT, alarm.getDestCoordinates().latitude);
        values.put(KEY_DEST_LONG, alarm.getDestCoordinates().longitude);
        values.put(KEY_PREP_TIME, alarm.getPrepTime());
        values.put(KEY_DEFAULT_ALARM, alarm.getDefaultAlarm());
        values.put(KEY_ETA, alarm.getEta());
        values.put(KEY_STATUS, alarm.getStatus());
        return values;
    }

    public Cursor fetchAllAlarms() {

        String ALARM_QUERY = String.format("select * from %s", TABLE_ALARMS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ALARM_QUERY, null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public void deleteAlarmById(long _id) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ALARMS, KEY_ID + " = ?", new String[] { String.valueOf(_id)});
    }

    public void updateAlarm(Alarm alarm) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = getContentValues(alarm);
        db.update(TABLE_ALARMS, values, KEY_ID + " = ?", new String[]{String.valueOf(alarm.getId())});
    }

    public Alarm getAlarm(long alarmId){
        Alarm alarm = null;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS + " WHERE "
                + KEY_ID + " = " + alarmId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
            alarm = new Alarm();
            alarm.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            alarm.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            alarm.setOrigin(c.getString(c.getColumnIndex(KEY_ORIGIN)));
            alarm.setOriginCoordinates(new LatLng(c.getDouble(c.getColumnIndex(KEY_ORIGIN_LAT)), c.getDouble(c.getColumnIndex(KEY_ORIGIN_LONG))));
            alarm.setDestination(c.getString(c.getColumnIndex(KEY_DEST)));
            alarm.setDestCoordinates(new LatLng(c.getDouble(c.getColumnIndex(KEY_DEST_LAT)), c.getDouble(c.getColumnIndex(KEY_DEST_LONG))));
            alarm.setPrepTime(c.getInt(c.getColumnIndex(KEY_PREP_TIME)));
            alarm.setEta(c.getLong(c.getColumnIndex(KEY_ETA)));
            alarm.setDefaultAlarm(c.getLong(c.getColumnIndex(KEY_DEFAULT_ALARM)));
            alarm.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
        }
        return alarm;
    }
}
