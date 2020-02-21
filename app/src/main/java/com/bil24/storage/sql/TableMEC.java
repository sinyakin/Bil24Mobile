package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.*;
import android.util.Log;
import com.bil24.storage.sql.db.MyExtraTicket;
import server.net.obj.extra.ExtraMEC;

import java.math.BigDecimal;
import java.util.*;

/**
 * Виктор
 * 18.08.2015.
 */
public class TableMEC extends Table {
  public static final String TABLE_NAME = "mec";
  public static final String TICKET_ID = "ticketId";
  public static final String ACTION_EVENT_ID = "actionEventId";
  public static final String SUM = "sum";
  public static final String CATEGORY_NAME = "categoryName";
  public static final String QR_CODE_IMAGE = "qrCodeImg";
  public static final String BAR_CODE_IMAGE = "barCodeImg";
  public static final String BAR_CODE_NUMBER = "barCodeNumber";

  public TableMEC(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        TICKET_ID + " bigint PRIMARY KEY, " +
        ACTION_EVENT_ID + " bigint, " +
        SUM + " text not null, " +
        CATEGORY_NAME + " text not null, " +
        QR_CODE_IMAGE + " text, " +
        BAR_CODE_IMAGE + " text, " +
        BAR_CODE_NUMBER + " text)";
  }

  public void addMEC(long ticketId, long actionEventId, BigDecimal sum,
                        String categoryName, String qrCodeImg, String barCodeImg, String barCodeNumber) {
    try {
      ContentValues values = new ContentValues();
      values.put(TICKET_ID, ticketId);
      values.put(ACTION_EVENT_ID, actionEventId);
      values.put(SUM, sum.toString());
      values.put(CATEGORY_NAME, categoryName);
      values.put(QR_CODE_IMAGE, qrCodeImg);
      values.put(BAR_CODE_IMAGE, barCodeImg);
      values.put(BAR_CODE_NUMBER, barCodeNumber);
      SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
      sqLiteDatabase.insertOrThrow(TableMEC.TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
  }

  @Nullable
  public ExtraMEC getMECById(long ticketId) {
    ExtraMEC result = null;
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, TICKET_ID + " = " + ticketId);
      if (cursor.moveToNext()) {
        result = getExtraMEC(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  @NonNull
  public List<ExtraMEC> getMECsByActionEventId(long actionEventId) {
    List<ExtraMEC> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, ACTION_EVENT_ID + " = " + actionEventId, TICKET_ID, ORDERING.DESC);
      while (cursor.moveToNext()) {
        ExtraMEC ticket = getExtraMEC(cursor);
        result.add(ticket);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  public void updateMEC(long ticketId, String column, String value) {
    try {
      ContentValues values = new ContentValues();
      values.put(column, value);
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(TABLE_NAME, values, TICKET_ID + "=" + ticketId, null);
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

  public void delMECsByActionEvent(long actionEventId) {
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.delete(TABLE_NAME, ACTION_EVENT_ID + "=" + actionEventId, null);
    } catch (Exception ex) {
      log(ex);
    }
  }

  private ExtraMEC getExtraMEC(Cursor cursor) {
    return new ExtraMEC(
        getLong(cursor, TICKET_ID),
        getString(cursor, CATEGORY_NAME),
        new BigDecimal(getString(cursor, SUM)),
        getString(cursor, QR_CODE_IMAGE),
        getString(cursor, BAR_CODE_IMAGE),
        getString(cursor, BAR_CODE_NUMBER));
  }
}
