package com.example.android.autosend.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.autosend.data.Alarm;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Ishmita on 09-01-2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "autoSend";
    private static final String TABLE_NAME = "alarm";

    //column names
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String CONTACT_NAME = "name";
    private static final String CONTACT_NUMBER = "phone";
    private static final String MESSAGE = "message";
    private static final String DATE = "date";
    private static final String STATUS = "status";
    private Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    //create tables in this method
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String create_table_query = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                TITLE +" VARCHAR(20)," +
                CONTACT_NAME + " VARCHAR(20)," +
                CONTACT_NUMBER + " VARCHAR(20)," +
                MESSAGE + " TEXT," +
                DATE + " TEXT," +
                STATUS + " INTEGER" + ")";
        sqLiteDatabase.execSQL(create_table_query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //save alarm
    public long saveAlarm(Alarm alarm) {
        long id = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //contentValues.put(ID, alarm.getId());
        contentValues.put(TITLE, alarm.getAlarmTitle());
        contentValues.put(CONTACT_NAME, alarm.getContactName());
        contentValues.put(CONTACT_NUMBER, alarm.getContactNumber());
        contentValues.put(MESSAGE, alarm.getMessage());
        contentValues.put(DATE, formatDateTime(mContext,alarm.getDate()));
        contentValues.put(STATUS, alarm.getStatus());
        id = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return id;
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy MM dd HH mm");

        Date date = null;
        if (timeToFormat != null) {
            try {
                //convert String date into Date type
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                e.printStackTrace();
                date = null;
                Log.d(TAG, "date is null");
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when, flags);
                //Log.d(TAG, "when: " + when+" after adding offset: "+ when +
                        //TimeZone.getDefault().getOffset(when));
            }
        }
        Log.d(TAG, "formatted date: " + finalDateTime);
        return finalDateTime;
    }
    //get all alarms
    public ArrayList<Alarm> getAllAlarms() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ArrayList<Alarm> alarms = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));
                alarm.setAlarmTitle(cursor.getString(1));
                alarm.setContactName(cursor.getString(2));
                alarm.setContactNumber(cursor.getString(3));
                alarm.setMessage(cursor.getString(4));
                alarm.setDate(cursor.getString(5));
                alarm.setStatus(cursor.getInt(6));
                alarms.add(alarm);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return alarms;
    }

    //get a single alarm based on Id
    public Alarm getAlarm(int id) {
        Alarm alarm=null;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, new String[] { ID,
                        TITLE, CONTACT_NAME, CONTACT_NUMBER, MESSAGE, DATE, STATUS }, ID + " = " + id,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            alarm = new Alarm(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6));
        }
        Log.d(TAG, "alarm: "+alarm);
        cursor.close();
        sqLiteDatabase.close();
        return alarm;
    }
    //edit an alarm
    public int editAlarm(Alarm alarm) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //contentValues.put(ID, alarm.getId());
        contentValues.put(TITLE, alarm.getAlarmTitle());
        contentValues.put(CONTACT_NAME, alarm.getContactName());
        contentValues.put(CONTACT_NUMBER, alarm.getContactNumber());
        contentValues.put(MESSAGE, alarm.getMessage());
        contentValues.put(DATE, alarm.getDate());
        contentValues.put(STATUS, alarm.getStatus());
        return sqLiteDatabase.update(TABLE_NAME, contentValues, ID + " = ?",
                new String[]{String.valueOf(alarm.getId())});
    }
    //delete an alarm
    public void deleteAlarm(Alarm alarm) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, ID + " = ?",
                new String[]{String.valueOf(alarm.getId())});
        sqLiteDatabase.close();
    }
    //delete all
    public int deleteAllAlarms() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, null, null);
    }
}
