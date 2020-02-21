package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.NonNull;
import com.bil24.storage.Session;

/**
 * Виктор
 * 18.08.2015.
 */
@Deprecated //todo удалить когда версия приложения у всех будет 29
public class TableUserData extends Table {

  private static final class Cols {
    static final String TABLE_NAME = "bil24data";
    static final String ID = "id";
    static final String USER_ID = "userId";
    static final String SESSION_ID = "sessionId";
  }

  private static final int DEFAULT_DATA_ID = 1;
  private static final long DEFAULT_USER_ID = 0L;
  private static final String DEFAULT_SESSION_ID = "";

  TableUserData(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + Cols.TABLE_NAME + " (" +
        Cols.ID + " integer PRIMARY KEY, " +
        Cols.USER_ID + " bigint not null, " +
        Cols.SESSION_ID + " text not null)";
  }

  void addDefaultData(SQLiteDatabase sqLiteDatabase) {
    ContentValues values = new ContentValues();
    values.put(Cols.ID, DEFAULT_DATA_ID);
    values.put(Cols.USER_ID, DEFAULT_USER_ID);
    values.put(Cols.SESSION_ID, DEFAULT_SESSION_ID);
    sqLiteDatabase.insertOrThrow(Cols.TABLE_NAME, null, values);
  }

  @NonNull
  public Session getSession(SQLiteDatabase db) {
    Session result = null;
    Cursor cursor = null;
    try {
      cursor = query(db, Cols.TABLE_NAME, Cols.ID + " = " + DEFAULT_DATA_ID);
      if (cursor.moveToNext()) {
        result = getSession(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    if (result == null) return new Session(DEFAULT_USER_ID, DEFAULT_SESSION_ID, DEFAULT_SESSION_ID);
    return result;
  }

  @NonNull
  public Session getSession() {
    Session result = null;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor = null;
    try {
      sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, Cols.TABLE_NAME, Cols.ID + " = " + DEFAULT_DATA_ID);
      if (cursor.moveToNext()) {
        result = getSession(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    if (result == null) return new Session(DEFAULT_USER_ID, DEFAULT_SESSION_ID, DEFAULT_SESSION_ID);
    return result;
  }

  void clearSession() {
    updateSession(DEFAULT_USER_ID, DEFAULT_SESSION_ID);
  }

  public boolean updateSession(Long userId, String sessionId) {
    SQLiteDatabase sqLiteDatabase;
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.USER_ID, userId);
      values.put(Cols.SESSION_ID, sessionId);
      sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
    } catch (Exception ex) {
      log(ex);
      return false;
    }
    return true;
  }

  private Session getSession(Cursor cursor) {
    Long userId = getLong(cursor, Cols.USER_ID);
    String sessionId = getString(cursor, Cols.SESSION_ID);
    return new Session(userId, sessionId, "");
  }
}
