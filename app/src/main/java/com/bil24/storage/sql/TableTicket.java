package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.*;
import android.util.Log;
import com.bil24.storage.sql.db.MyExtraTicket;

import java.math.BigDecimal;
import java.util.*;

/**
 * Виктор
 * 18.08.2015.
 */
public class TableTicket extends Table {
  public static final String TABLE_NAME = "ticket";
  public static final String TICKET_ID = "ticketId";
  public static final String ACTION_EVENT_ID = "actionEventId";
  public static final String ACTION_NAME = "actionName";
  public static final String VENUE_NAME = "venueName";
  public static final String VENUE_ADDRESS = "venueAddress";
  public static final String DATE = "date";
  public static final String SUM = "sum";
  public static final String SECTOR = "sector";
  public static final String ROW = "row";
  public static final String NUMBER = "number";
  public static final String CATEGORY_NAME = "categoryName";
  public static final String QR_CODE_IMAGE = "qrCodeImg";
  public static final String BAR_CODE_IMAGE = "barCodeImg";
  public static final String BAR_CODE_NUMBER = "barCodeNumber";
  public static final String SHOW = "show";


  public TableTicket(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        TICKET_ID + " bigint PRIMARY KEY, " +
        ACTION_EVENT_ID + " bigint, " +
        ACTION_NAME + " text not null, " +
        VENUE_NAME + " text not null, " +
        VENUE_ADDRESS + " text not null, " +
        DATE + " text not null, " +
        SUM + " text not null, " +
        SECTOR + " text not null, " +
        ROW + " text not null, " +
        NUMBER + " text not null, " +
        CATEGORY_NAME + " text not null, " +
        QR_CODE_IMAGE + " text, " +
        SHOW + " integer, " +
        BAR_CODE_IMAGE + " text, " +
        BAR_CODE_NUMBER + " text)";
  }

  public void addTicket(Long ticketId, Long actionEventId, String actionName, String venueName,
                        String venueAddress, String date, String sum,
                        String sector, String row, String number, String categoryName, String qrCodeImg, String barCodeImg, String barCodeNumber) {
    try {
      ContentValues values = new ContentValues();
      values.put(TICKET_ID, ticketId);
      values.put(ACTION_EVENT_ID, actionEventId);
      values.put(ACTION_NAME, actionName);
      values.put(VENUE_NAME, venueName);
      values.put(VENUE_ADDRESS, venueAddress);
      values.put(DATE, date);
      values.put(SUM, sum);
      values.put(SECTOR, field(sector));
      values.put(ROW, field(row));
      values.put(NUMBER, field(number));
      values.put(CATEGORY_NAME, categoryName);
      values.put(QR_CODE_IMAGE, qrCodeImg);
      values.put(BAR_CODE_IMAGE, barCodeImg);
      values.put(BAR_CODE_NUMBER, barCodeNumber);
      values.put(SHOW, 1);
      SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
      sqLiteDatabase.insertOrThrow(TableTicket.TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
  }

  private String field(String value) {
    return (value == null) ? "" : value;
  }

  private Long field(Long value) {
    return (value == null) ? 0 : value;
  }

  @Nullable
  public MyExtraTicket getTicketById(Long ticketId) {
    MyExtraTicket result = null;
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, TICKET_ID + " = " + ticketId);
      if (cursor.moveToNext()) {
        result = getExtraTicket(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  @NonNull
  public List<MyExtraTicket> getTicketsByActionEventId(Long actionEventId) {
    List<MyExtraTicket> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, ACTION_EVENT_ID + " = " + actionEventId, TICKET_ID, ORDERING.DESC);
      while (cursor.moveToNext()) {
        MyExtraTicket ticket = getExtraTicket(cursor);
        result.add(ticket);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  public void updateTicket(Long ticketId, String column, String value) {
    try {
      ContentValues values = new ContentValues();
      values.put(column, value);
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(TABLE_NAME, values, TICKET_ID + "=" + ticketId, null);
    } catch (Exception ex) {
      log(ex);
    }
  }

  public void delTicketByActionEvent(Long actionEventId) {
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.delete(TABLE_NAME, ACTION_EVENT_ID + "=" + actionEventId, null);
    } catch (Exception ex) {
      log(ex);
    }
  }

  public void updateTicketShow(Long ticketId, boolean show) {
    updateTicket(ticketId, SHOW, String.valueOf(show ? 1 : 0));
  }

  public void clearTicketTable() {
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      int id = sqLiteDatabase.delete(TableTicket.TABLE_NAME, null, null);
      Log.d("bil24|del", "id=" + id);
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

  private MyExtraTicket getExtraTicket(Cursor cursor) {
    String sector = getString(cursor, SECTOR);
    String row = getString(cursor, ROW);
    String number = getString(cursor, NUMBER);
    String categoryName = getString(cursor, CATEGORY_NAME); //не может быть Null
    if (sector.isEmpty()) sector = null;
    if (row.isEmpty()) row = null;
    if (number.isEmpty()) number = null;

    return new MyExtraTicket(
        getLong(cursor, ACTION_EVENT_ID),
        getString(cursor, ACTION_NAME),
        getLong(cursor, TICKET_ID),
        getString(cursor, DATE),
        getString(cursor, VENUE_NAME),
        getString(cursor, VENUE_ADDRESS),
        sector,
        row,
        number,
        categoryName,
        new BigDecimal(getString(cursor, SUM)),
        getString(cursor, QR_CODE_IMAGE),
        getString(cursor, BAR_CODE_IMAGE),
        getInteger(cursor, SHOW) == 1,
        getString(cursor, BAR_CODE_NUMBER));
  }
}