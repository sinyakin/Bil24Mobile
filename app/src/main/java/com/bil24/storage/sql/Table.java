package com.bil24.storage.sql;

import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Виктор
 * 18.08.2015.
 */
public class Table {

  @NonNull SQLiteOpenHelper helper;

  public Table(@NonNull SQLiteOpenHelper helper) {
    this.helper = helper;
  }

  Cursor query(SQLiteDatabase sqLiteDatabase, String tableName) {
    return sqLiteDatabase.query(tableName, null, null, null, null, null, null);
  }

  Cursor query(SQLiteDatabase sqLiteDatabase, String tableName, String query) {
    return sqLiteDatabase.query(tableName, null, query, null, null, null, null);
  }

  Cursor query(SQLiteDatabase sqLiteDatabase, String tableName, String query, String orderByField, ORDERING ordering) {
    return sqLiteDatabase.query(tableName, null, query, null, null, null, orderByField + " " + ordering.name());
  }

  Long getLong(Cursor cursor, String columnName) {
    return cursor.getLong(cursor.getColumnIndex(columnName));
  }

  Integer getInteger(Cursor cursor, String columnName) {
    return cursor.getInt(cursor.getColumnIndex(columnName));
  }

  String getString(Cursor cursor, String columnName) {
    return cursor.getString(cursor.getColumnIndex(columnName));
  }

  void close(Cursor cursor) {
    if (cursor != null) cursor.close();
  }

 /* void close(SQLiteDatabase sqLiteDatabase) {
    if (sqLiteDatabase != null) sqLiteDatabase.close();
  }*/

  enum ORDERING {
    ASC, DESC
  }

  public static void log(String text) {
    Log.d("bil24|db|", text);
  }

  public static void log(Exception ex) {
    Log.d("bil24|db|Exception|", String.valueOf(ex.getMessage()));
    ex.printStackTrace();
  }
}
