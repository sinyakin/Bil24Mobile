package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.*;
import com.bil24.storage.sql.db.MyActionEvent;
import com.bil24.utils.Utils;
import server.net.obj.extra.ExtraActionEventsGroupedByTickets;

import java.math.BigDecimal;
import java.util.*;

/**
 * Виктор
 * 12.05.2016.
 */
public class TableActionEvent extends Table {
  public static final String TABLE_NAME = "actionEvent";
  public static final String ACTION_EVENT_ID = "id";
  public static final String ACTION_NAME = "actionName";
  public static final String FULL_ACTION_NAME = "fullActionName";
  public static final String ACTION_EVENT_DAY = "actionEventDay";
  public static final String ACTION_EVENT_TIME = "actionEventTime";
  public static final String ACTION_EVENT_DATE = "actionEventDate";
  public static final String ACTION_BIG_POSTER = "bigPoster";
  public static final String ACTION_SMALL_POSTER = "smallPoster";
  public static final String CITY_NAME = "cityName";
  public static final String VENUE_NAME = "venueName";
  public static final String QUANTITY = "quantity";
  public static final String SUM = "sum";
  public static final String ACTION_DELETE = "del";

  public TableActionEvent(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        ACTION_EVENT_ID + " bigint PRIMARY KEY, " +
        ACTION_NAME + " text not null, " +
        FULL_ACTION_NAME + " text not null, " +
        ACTION_EVENT_DAY + " text not null, " +
        ACTION_EVENT_TIME + " text not null, " +
        ACTION_EVENT_DATE + " bigint, " +
        ACTION_BIG_POSTER + " text not null, " +
        ACTION_SMALL_POSTER + " text not null, " +
        CITY_NAME + " text not null, " +
        VENUE_NAME + " text not null, " +
        QUANTITY + " integer, "+
        SUM + " text not null, " +
        ACTION_DELETE + " integer)";
  }

  public void addActionEvent(ExtraActionEventsGroupedByTickets o, String dayPattern, String timePattern) {
    try {
      Long date = Utils.dateFormat(o.getDay() + " " + o.getTime(), dayPattern + " " + timePattern).getTime();

      ContentValues values = new ContentValues();
      values.put(ACTION_EVENT_ID, o.getActionEventId());
      values.put(ACTION_NAME, o.getActionName());
      values.put(FULL_ACTION_NAME, o.getFullActionName());
      values.put(ACTION_EVENT_DAY, o.getDay());
      values.put(ACTION_EVENT_TIME, o.getTime());
      values.put(ACTION_EVENT_DATE, date);
      values.put(ACTION_BIG_POSTER, o.getBigPosterUrl());
      values.put(ACTION_SMALL_POSTER, o.getSmallPosterUrl());
      values.put(CITY_NAME, o.getCityName());
      values.put(VENUE_NAME, o.getVenueName());
      values.put(QUANTITY, o.getQuantity());
      values.put(SUM, o.getSum().toString());
      values.put(ACTION_DELETE, 0);
      SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
      sqLiteDatabase.insertOrThrow(TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
  }

  @Nullable
  public MyActionEvent getActionEventById(Long actionEventId) {
    MyActionEvent result = null;
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, ACTION_EVENT_ID + " = " + actionEventId);
      if (cursor.moveToNext()) {
        result = getExtraActionEvent(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  @NonNull
  public List<MyActionEvent> getActions() {
    List<MyActionEvent> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, ACTION_DELETE + " = 0", ACTION_EVENT_DATE, ORDERING.DESC);
      while (cursor.moveToNext()) {
        MyActionEvent actionEvent = getExtraActionEvent(cursor);
        result.add(actionEvent);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  public void updateData(ExtraActionEventsGroupedByTickets o) {
    ContentValues values = new ContentValues();
    values.put(ACTION_BIG_POSTER, o.getBigPosterUrl());
    values.put(ACTION_SMALL_POSTER, o.getSmallPosterUrl());
    values.put(QUANTITY, o.getQuantity());
    values.put(SUM, o.getSum().toString());
    updateActionEvent(o.getActionEventId(), values);
  }

  public void delActionEvent(Long actionEventId) {
    ContentValues values = new ContentValues();
    values.put(ACTION_DELETE, "1");
    updateActionEvent(actionEventId, values);
  }

  private void updateActionEvent(Long actionEventId, ContentValues values) {
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(TABLE_NAME, values, ACTION_EVENT_ID + "=" + actionEventId, null);
    } catch (Exception ex) {
      log(ex);
    }
  }

  void dropTable(SQLiteDatabase db) {
    db.beginTransaction();
    try {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      db.execSQL(createTable());
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }

  void clearTable(SQLiteDatabase db) {
    db.delete(TABLE_NAME, "1", null);
  }

  private MyActionEvent getExtraActionEvent(Cursor cursor) {
    return new MyActionEvent(
        getLong(cursor, ACTION_EVENT_ID),
        getString(cursor, ACTION_NAME),
        getString(cursor, FULL_ACTION_NAME),
        getString(cursor, ACTION_EVENT_DAY),
        getString(cursor, ACTION_EVENT_TIME),
        getString(cursor, ACTION_BIG_POSTER),
        getString(cursor, ACTION_SMALL_POSTER),
        getString(cursor, CITY_NAME),
        getString(cursor, VENUE_NAME),
        getInteger(cursor, QUANTITY),
        new BigDecimal(getString(cursor, SUM)),
        getLong(cursor, ACTION_EVENT_DATE),
        getInteger(cursor, ACTION_DELETE)
    );
  }
}
