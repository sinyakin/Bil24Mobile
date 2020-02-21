package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.bil24.storage.FrontendData;
import com.bil24.storage.FrontendType;
import com.bil24.storage.Session;
import com.bil24.storage.SettingsCommon;
import com.bil24.utils.Utils;

/**
 * Виктор
 * 18.08.2015.
 */
public class TableMainData extends Table {

  private static final class Cols {
    static final String TABLE_NAME = "bil24maindata";
    static final String ID = "id";
    static final String USER_ID = "userId";
    static final String SESSION_ID = "sessionId";
    static final String EMAIL = "email";

    static final String DEFAULT_FID = "defaultFid";
    static final String DEFAULT_TOKEN = "defaultToken";
    static final String DEFAULT_FRONTEND_TYPE = "defaultFrontendType";

    static final String NEW_FID = "newFid";
    static final String NEW_TOKEN = "newToken";
    static final String NEW_FRONTEND_TYPE = "newFrontendType";

    static final String REFERRER_AUTH = "referrerAuth";
  }

  private static final int DEFAULT_DATA_ID = 1;
  private static final long DEFAULT_USER_ID = 0L;
  private static final String DEFAULT_SESSION_ID = "";

  public static final long MAIN_FID = 1300L;
  public static final String MAIN_TOKEN = "8bd15425f7c448620105";
//  public static final long MAIN_FID = 1033L; //для третьей тестовой зоны
//  public static final String MAIN_TOKEN = "ca800da092811cf9c2be";//для третьей тестовой зоны
  public static final FrontendType MAIN_FRONTEND_TYPE = FrontendType.ANDROID;

  public static final long MAIN_FID_TEST_ZONE = 1001L;
  public static final String MAIN_TOKEN_TESTZONE = "f463c39897b775d226c9d";

  TableMainData(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + Cols.TABLE_NAME + " (" +
        Cols.ID + " integer PRIMARY KEY, " +
        Cols.USER_ID + " bigint not null, " +
        Cols.SESSION_ID + " text not null, " +
        Cols.EMAIL + " text not null, " +
        Cols.REFERRER_AUTH + " integer, " +
        Cols.DEFAULT_FID + " bigint not null, " +
        Cols.DEFAULT_TOKEN + " text not null, " +
        Cols.DEFAULT_FRONTEND_TYPE + " integer, " +
        Cols.NEW_FID + " bigint not null, " +
        Cols.NEW_TOKEN + " text not null, " +
        Cols.NEW_FRONTEND_TYPE + " integer)";
  }

  void addDefaultData(SQLiteDatabase sqLiteDatabase) {
    ContentValues values = new ContentValues();
    values.put(Cols.ID, DEFAULT_DATA_ID);
    values.put(Cols.USER_ID, DEFAULT_USER_ID);
    values.put(Cols.SESSION_ID, DEFAULT_SESSION_ID);
    values.put(Cols.EMAIL, "");
    values.put(Cols.REFERRER_AUTH, 0);
    long fid = SettingsCommon.isRealZone() ? MAIN_FID: MAIN_FID_TEST_ZONE;
    String token = SettingsCommon.isRealZone() ? MAIN_TOKEN : MAIN_TOKEN_TESTZONE;
    values.put(Cols.DEFAULT_FID, fid);
    values.put(Cols.DEFAULT_TOKEN, token);
    values.put(Cols.DEFAULT_FRONTEND_TYPE, MAIN_FRONTEND_TYPE.getId());
    values.put(Cols.NEW_FID, Utils.DEFAULT_OBJECT_ID);
    values.put(Cols.NEW_TOKEN, "");
    values.put(Cols.NEW_FRONTEND_TYPE, Utils.DEFAULT_OBJECT_ID);
    sqLiteDatabase.insertOrThrow(Cols.TABLE_NAME, null, values);
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
    if (result == null) return new Session(DEFAULT_USER_ID, DEFAULT_SESSION_ID, "");
    return result;
  }

  @NonNull
  public FrontendData getFrontendData() {
    FrontendData result = null;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor = null;
    try {
      sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, Cols.TABLE_NAME, Cols.ID + " = " + DEFAULT_DATA_ID);
      if (cursor.moveToNext()) {
        result = getFrontendData(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    if (result == null) return new FrontendData(MAIN_FID, MAIN_TOKEN, MAIN_FRONTEND_TYPE, Utils.DEFAULT_OBJECT_ID, "", MAIN_FRONTEND_TYPE);
    return result;
  }

  void clearSession() {
    updateSession(DEFAULT_USER_ID, DEFAULT_SESSION_ID);
  }

  @Deprecated
  void updateSession(SQLiteDatabase db, long userId, String sessionId, String email) {
    ContentValues values = new ContentValues();
    values.put(Cols.USER_ID, userId);
    values.put(Cols.SESSION_ID, sessionId);
    values.put(Cols.EMAIL, email);
    db.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
  }

  public boolean updateSession(long userId, @NonNull String sessionId) {
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

  public boolean updateEmail(@NonNull String email) {
    SQLiteDatabase sqLiteDatabase;
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.EMAIL, email);
      sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
    } catch (Exception ex) {
      log(ex);
      return false;
    }
    return true;
  }

  public boolean setMainFrontendData() {
    return setDefaultFrontendData(MAIN_FID, MAIN_TOKEN, MAIN_FRONTEND_TYPE);
  }

  @Deprecated
  void setDefaultFrontendData(SQLiteDatabase db, long fid, String token, FrontendType frontendType) {
    ContentValues values = new ContentValues();
    values.put(Cols.DEFAULT_FID, fid);
    values.put(Cols.DEFAULT_TOKEN, token);
    values.put(Cols.DEFAULT_FRONTEND_TYPE, frontendType.getId());
    values.put(Cols.NEW_FID, Utils.DEFAULT_OBJECT_ID);
    values.put(Cols.NEW_TOKEN, "");
    values.put(Cols.NEW_FRONTEND_TYPE, Utils.DEFAULT_OBJECT_ID);
    db.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
  }

  //использовать только при старте приложения
  public boolean setDefaultFrontendData(long fid, String token, FrontendType frontendType) {
    SQLiteDatabase sqLiteDatabase;
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.DEFAULT_FID, fid);
      values.put(Cols.DEFAULT_TOKEN, token);
      values.put(Cols.DEFAULT_FRONTEND_TYPE, frontendType.getId());
      values.put(Cols.NEW_FID, Utils.DEFAULT_OBJECT_ID);
      values.put(Cols.NEW_TOKEN, "");
      values.put(Cols.NEW_FRONTEND_TYPE, Utils.DEFAULT_OBJECT_ID);
      sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
    } catch (Exception ex) {
      log(ex);
      return false;
    }
    return true;
  }

  public boolean setNewDefaultFrontendData() {
    SQLiteDatabase sqLiteDatabase;
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.DEFAULT_FID, MAIN_FID);
      values.put(Cols.DEFAULT_TOKEN, MAIN_TOKEN);
      sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
    } catch (Exception ex) {
      log(ex);
      return false;
    }
    return true;
  }

  public boolean resetNewFrontendData() {
    SQLiteDatabase sqLiteDatabase;
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.NEW_FID, Utils.DEFAULT_OBJECT_ID);
      values.put(Cols.NEW_TOKEN, "");
      values.put(Cols.NEW_FRONTEND_TYPE, Utils.DEFAULT_OBJECT_ID);
      sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
    } catch (Exception ex) {
      log(ex);
      return false;
    }
    return true;
  }

  @Deprecated
  public void setNewFrontendData(SQLiteDatabase db, long fid, String token, FrontendType frontendType) {
    ContentValues values = new ContentValues();
    values.put(Cols.NEW_FID, fid);
    values.put(Cols.NEW_TOKEN, token);
    values.put(Cols.NEW_FRONTEND_TYPE, frontendType.getId());
    db.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
  }

  public boolean setNewFrontendData(long fid, String token, FrontendType frontendType) {
    SQLiteDatabase sqLiteDatabase;
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.NEW_FID, fid);
      values.put(Cols.NEW_TOKEN, token);
      values.put(Cols.NEW_FRONTEND_TYPE, frontendType.getId());
      sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
    } catch (Exception ex) {
      log(ex);
      return false;
    }
    return true;
  }

  public boolean setReferrerAuth(boolean referrerAuth) {
    SQLiteDatabase sqLiteDatabase;
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.REFERRER_AUTH, referrerAuth ? 1 : 0);
      sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.ID + "=" + DEFAULT_DATA_ID, null);
    } catch (Exception ex) {
      log(ex);
      return false;
    }
    return true;
  }

  public boolean isReferrerAuth() {
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor = null;
    try {
      sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, Cols.TABLE_NAME, Cols.ID + " = " + DEFAULT_DATA_ID);
      return cursor.moveToNext() && getInteger(cursor, Cols.REFERRER_AUTH) == 1;
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return false;
  }

  private Session getSession(Cursor cursor) {
    Long userId = getLong(cursor, Cols.USER_ID);
    String sessionId = getString(cursor, Cols.SESSION_ID);
    String email = getString(cursor, Cols.EMAIL);
    return new Session(userId, sessionId, email);
  }

  private FrontendData getFrontendData(Cursor cursor) {
    return new FrontendData(
        getLong(cursor, Cols.DEFAULT_FID),
        getString(cursor, Cols.DEFAULT_TOKEN),
        FrontendType.get(getInteger(cursor, Cols.DEFAULT_FRONTEND_TYPE)),
        getLong(cursor, Cols.NEW_FID),
        getString(cursor, Cols.NEW_TOKEN),
        FrontendType.get(getInteger(cursor, Cols.NEW_FRONTEND_TYPE)));
  }
}
