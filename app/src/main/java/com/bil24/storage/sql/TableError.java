package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.NonNull;
import com.bil24.Controller;
import com.bil24.storage.sql.db.MyError;
import com.bil24.utils.ApplicationInfo;

import java.util.*;

/**
 * Виктор
 * 18.08.2015.
 */
public class TableError extends Table {
  public static final String TABLE_NAME = "error";
  private static final String ERROR_ID = "errorId";
  private static final String DEVICE_NAME = "deviceName";
  private static final String VERSION_NAME = "versionName";
  private static final String VERSION_CODE = "versionCode";
  private static final String ERROR_AS_STRING = "errorAsString";

  TableError(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        ERROR_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
        DEVICE_NAME + " text not null, " +
        VERSION_NAME + " text not null, " +
        VERSION_CODE + " text not null, " +
        ERROR_AS_STRING + " text not null)";
  }

  public void addError(@NonNull ApplicationInfo applicationInfo, @NonNull String errorAsString) {
    try {
      ContentValues values = new ContentValues();
      values.put(DEVICE_NAME, applicationInfo.getDevice());
      values.put(VERSION_CODE, applicationInfo.getVersionCode());
      values.put(VERSION_NAME, applicationInfo.getVersionName());
      values.put(ERROR_AS_STRING, errorAsString);
      SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
      sqLiteDatabase.insertOrThrow(TableError.TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
    Controller.getInstance().sendToServiceWakeup();
  }

  @NonNull
  public List<MyError> getErrorsNotSend() {
    List<MyError> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME);
      while (cursor.moveToNext()) {
        MyError pass = getError(cursor);
        result.add(pass);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  public void delErrorById(Integer errorId) {
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.delete(TABLE_NAME, ERROR_ID + "=" + errorId, null);
    } catch (Exception ex) {
      log(ex);
    }
  }

  private MyError getError(Cursor cursor) {
    return new MyError(
        getInteger(cursor, ERROR_ID),
        getString(cursor, DEVICE_NAME),
        getString(cursor, VERSION_NAME),
        getString(cursor, VERSION_CODE),
        getString(cursor, ERROR_AS_STRING));
  }
}
