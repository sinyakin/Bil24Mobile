package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.*;

import com.bil24.storage.Settings;

import server.net.obj.extra.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Виктор
 * 12.05.2016.
 */
public class TableMECInfo extends Table {
  public static final String TABLE_NAME = "mecInfo";
  public static final String ID = "id";
  public static final String ACTION_EVENT_ID = "actionEventId";
  public static final String ACTION_NAME = "actionName";
  public static final String FULL_ACTION_NAME = "fullActionName";
  public static final String ACTION_BIG_POSTER = "bigPoster";
  public static final String ACTION_SMALL_POSTER = "smallPoster";
  public static final String CITY_PASS = "cityPass";
  public static final String CITY_ID = "cityId";
  public static final String CITY_NAME = "cityName";
  public static final String VENUE_NAME = "venueName";
  public static final String VENUE_ADDRESS = "venueAddress";
  public static final String QUANTITY = "quantity";
  public static final String SUM = "sum";
  public static final String DELETE = "del";

  public TableMECInfo(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        ID + " integer PRIMARY KEY AUTOINCREMENT, " +
        ACTION_EVENT_ID + " bigint, " +
        ACTION_NAME + " text not null, " +
        FULL_ACTION_NAME + " text not null, " +
        ACTION_BIG_POSTER + " text not null, " +
        ACTION_SMALL_POSTER + " text not null, " +
        CITY_PASS + " integer, " +
        CITY_ID + " bigint, " +
        CITY_NAME + " text not null, " +
        VENUE_NAME + " text not null, " +
        VENUE_ADDRESS + " text not null, " +
        QUANTITY + " integer, "+
        SUM + " text not null, " +
        DELETE + " integer)";
  }

  public void addMecInfo(ExtraMECInfo o) {
    try {
      ContentValues values = new ContentValues();
      values.put(ACTION_EVENT_ID, o.getActionEventId());
      values.put(ACTION_NAME, o.getActionName());
      values.put(FULL_ACTION_NAME, o.getFullActionName());
      values.put(ACTION_BIG_POSTER, o.getBigPosterUrl());
      values.put(ACTION_SMALL_POSTER, o.getSmallPosterUrl());
      values.put(CITY_PASS, o.isCityPass() ? 1 : 0);
      values.put(CITY_ID, o.getCityId());
      values.put(CITY_NAME, o.getCityName());
      values.put(VENUE_NAME, o.getVenueName());
      values.put(VENUE_ADDRESS, o.getVenueAddress());
      values.put(QUANTITY, o.getQuantity());
      values.put(SUM, o.getTotalSum().toString());
      values.put(DELETE, 0);
      SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
      sqLiteDatabase.insertOrThrow(TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
  }

  @Nullable
  public ExtraMECInfo getMECInfoByActionEventId(long actionEventId) {
    ExtraMECInfo result = null;
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, ACTION_EVENT_ID + " = " + actionEventId);
      if (cursor.moveToNext()) {
        result = getExtraMECInfo(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  @NonNull
  public List<ExtraMECInfo> getMECsInfo() {
    List<ExtraMECInfo> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, DELETE + " = 0", ID, ORDERING.DESC);
      while (cursor.moveToNext()) {
        ExtraMECInfo actionEvent = getExtraMECInfo(cursor);
        result.add(actionEvent);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  public void updateData(ExtraMECInfo o) {
    ContentValues values = new ContentValues();
    values.put(ACTION_BIG_POSTER, o.getBigPosterUrl());
    values.put(ACTION_SMALL_POSTER, o.getSmallPosterUrl());
    values.put(QUANTITY, o.getQuantity());
    values.put(SUM, o.getTotalSum().toString());
    values.put(DELETE, 0);
    updateMECInfo(o.getActionEventId(), values);
  }

  public void delMECInfo(Long actionEventId) {
    ContentValues values = new ContentValues();
    values.put(DELETE, "1");
    updateMECInfo(actionEventId, values);
  }

  private void updateMECInfo(long actionEventId, ContentValues values) {
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(TABLE_NAME, values, ACTION_EVENT_ID + "=" + actionEventId, null);
    } catch (Exception ex) {
      log(ex);
    }
  }

  String dropTable() {
    Settings.setMECId(0);
    return "DROP TABLE IF EXISTS " + TABLE_NAME;
  }

  void clearTable(SQLiteDatabase db) {
    db.delete(TABLE_NAME, "1", null);
  }

  private ExtraMECInfo getExtraMECInfo(Cursor cursor) {
    return new ExtraMECInfo(
        getLong(cursor, ACTION_EVENT_ID),
        getString(cursor, ACTION_NAME),
        getString(cursor, FULL_ACTION_NAME),
        getString(cursor, ACTION_BIG_POSTER),
        getString(cursor, ACTION_SMALL_POSTER),
        getLong(cursor, CITY_ID),
        getString(cursor, CITY_NAME),
        getString(cursor, VENUE_NAME),
        getString(cursor, VENUE_ADDRESS),
        getInteger(cursor, CITY_PASS) == 1,
        getInteger(cursor, QUANTITY),
        new BigDecimal(getString(cursor, SUM)),
        new ArrayList<ExtraMEC>()
    );
  }
}
