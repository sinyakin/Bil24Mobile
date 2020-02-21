package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.*;
import android.database.sqlite.*;
import android.support.annotation.*;
import com.bil24.storage.sql.db.MyActionEvent;

import java.util.*;

/**
 * Виктор
 * 18.08.2015.
 */
@Deprecated
public class TableActionEventOld extends Table {
  public static final String TABLE_NAME = "actionEvent";
  public static final String ACTION_EVENT_ID = "id";
  public static final String ACTION_NAME = "actionName";
  public static final String ACTION_EVENT_DAY = "actionEventDay";
  public static final String ACTION_EVENT_TIME = "actionEventTime";
  public static final String ACTION_EVENT_DATE = "actionEventDate";
  public static final String ACTION_BIG_POSTER = "bigPoster";
  public static final String ACTION_SMALL_POSTER = "smallPoster";
  public static final String ACTION_DELETE = "del";

  public TableActionEventOld(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        ACTION_EVENT_ID + " bigint PRIMARY KEY, " +
        ACTION_NAME + " text not null, " +
        ACTION_EVENT_DAY + " text not null, " +
        ACTION_EVENT_TIME + " text not null, " +
        ACTION_EVENT_DATE + " bigint, " +
        ACTION_BIG_POSTER + " text not null, " +
        ACTION_SMALL_POSTER + " text not null, " +
        ACTION_DELETE + " integer)";
  }

  public void addActionEvent(Long actionEventId, String actionName, String day, String time, long date, String bigUrl, String smallUrl) {
    try {
      ContentValues values = new ContentValues();
      values.put(ACTION_EVENT_ID, actionEventId);
      values.put(ACTION_NAME, actionName);
      values.put(ACTION_EVENT_DAY, day);
      values.put(ACTION_EVENT_TIME, time);
      values.put(ACTION_EVENT_DATE, date);
      values.put(ACTION_BIG_POSTER, bigUrl);
      values.put(ACTION_SMALL_POSTER, smallUrl);
      values.put(ACTION_DELETE, 0);
      SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
      sqLiteDatabase.insertOrThrow(TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
  }

  public long getActionEvents(SQLiteDatabase db) {
    try {
      return DatabaseUtils.queryNumEntries(db, TABLE_NAME, ACTION_DELETE + " = 0");
    } catch (Exception ex) {
      log(ex);
    }
    return 0;
  }

  @Nullable
  public MyActionEvent getActionEventById(Long actionEventId) {
    MyActionEvent result = null;
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, ACTION_EVENT_ID + " = " + actionEventId);
      if (cursor.moveToNext()) {
//        result = getExtraActionEvent(cursor);
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
//        MyActionEvent actionEvent = getExtraActionEvent(cursor);
//        result.add(actionEvent);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  public void updateSmallBigPosterUrl(Long actionEventId, String bigPosterUrl, String smallPosterUrl) {
    updateActionEvent(actionEventId, ACTION_BIG_POSTER, bigPosterUrl);
    updateActionEvent(actionEventId, ACTION_SMALL_POSTER, smallPosterUrl);
  }

  public void delActionEvent(Long actionEventId) {
    updateActionEvent(actionEventId, ACTION_DELETE, "1");
  }

  private void updateActionEvent(Long actionEventId, String column, String value) {
    try {
      ContentValues values = new ContentValues();
      values.put(column, value);
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
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }

  /*private MyActionEvent getExtraActionEvent(Cursor cursor) {
    return new MyActionEvent(
        getLong(cursor, ACTION_EVENT_ID),
        getString(cursor, ACTION_NAME),
        getString(cursor, ACTION_EVENT_DAY),
        getString(cursor, ACTION_EVENT_TIME),
        getString(cursor, ACTION_BIG_POSTER),
        getString(cursor, ACTION_SMALL_POSTER),
        getLong(cursor, ACTION_EVENT_DATE),
        getInteger(cursor, ACTION_DELETE)
    );
  }*/
}
