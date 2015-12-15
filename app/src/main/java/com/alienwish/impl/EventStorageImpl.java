package com.alienwish.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.alienwish.Event;
import com.alienwish.EventStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import rx.Observable;

/**
 * Created by Freyman on 15.12.2015.
 */
public class EventStorageImpl extends SQLiteOpenHelper implements EventStorage, BaseColumns {

    private static final String DATABASE_NAME = "alienwish.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_EVENTS = "table_events";
    private static final String TABLE_EVENTS_TEXT = "text";
    private static final String TABLE_EVENTS_CREATED_AT = "created_at";
    private static final String TABLE_EVENTS_ALERT_AT = "alert_at";

    private static final String DROP_TABLE_EVENTS_SCRIPT = "DROP TABLE IF EXISTS " + TABLE_EVENTS;
    private static final String CREATE_TABLE_EVENTS_SCRIPT = "CREATE TABLE " + TABLE_EVENTS + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TABLE_EVENTS_TEXT + " text not null, "
            + TABLE_EVENTS_CREATED_AT + " text not null, "
            + TABLE_EVENTS_ALERT_AT + " text not null, "
            + ");";

    private static final String ISO8601_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final String ISO8601_TIMEZONE = "UTC";

    private DateFormat createISO8601DateFormat() {
        DateFormat df = new SimpleDateFormat(ISO8601_DATETIME_PATTERN, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone(ISO8601_TIMEZONE));
        return df;
    }

    public EventStorageImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void clean() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(DROP_TABLE_EVENTS_SCRIPT);
        onCreate(db);
    }

    @Override
    public long addEvent(String text, Date alertAt) {
        DateFormat df = createISO8601DateFormat();
        String isoCreatedAt = df.format(new Date());
        String isoAlertAt = df.format(alertAt);

        SQLiteDatabase db = getReadableDatabase();

        ContentValues newValue = new ContentValues();
        newValue.put(TABLE_EVENTS_TEXT, text);
        newValue.put(TABLE_EVENTS_CREATED_AT, isoCreatedAt);
        newValue.put(TABLE_EVENTS_ALERT_AT, isoAlertAt);

        long id = db.insert(TABLE_EVENTS, null, newValue);

        if (id < 0) {
            throw new SQLException("An event with text '" + text + "' wasn't added into " + TABLE_EVENTS);
        }

        return id;
    }

    @Override
    public void removeEvent(long id) {

    }

    @Override
    public Observable<List<Event>> getEvents() {
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EVENTS_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clean();
    }
}
