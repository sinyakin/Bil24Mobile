package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.*;
import com.bil24.fragments.action.filter.Kind;

import java.util.*;

/**
 * Виктор
 * 18.08.2015.
 */
public class TableKind extends Table {
  public static final String TABLE_NAME = "kind";
  public static final String KIND_ID = "kindId";
  public static final String KIND_NAME = "kindName";
  public static final String SELECTED = "selected";

  public static final String INSERT = "INSERT INTO " + TABLE_NAME + " (kindId, kindName, selected) VALUES (?, ?, ?)";

  public static final Long allKindId = -1L;

  public TableKind(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        KIND_ID + " bigint PRIMARY KEY, " +
        KIND_NAME + " text not null, " +
        SELECTED + " integer)";
  }

  void addKindDefault(SQLiteDatabase db) {
    try {
      Kind kind = new Kind(0, "События");
      ContentValues values = new ContentValues();
      values.put(KIND_ID, kind.getKindId());
      values.put(KIND_NAME, kind.getKindName());
      values.put(SELECTED, 1);
      db.insertOrThrow(TableKind.TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
  }

  public void addKindDefault() {
    try {
      SQLiteDatabase db = helper.getWritableDatabase();
      addKindDefault(db);
      } catch (Exception ex) {
      log(ex);
    }
  }

  void addKinds(@NonNull List<Kind> kindList, SQLiteDatabase db) {
    if (kindList.isEmpty()) return;
    Kind kindSelected = getSelectedKind(db);
    deleteKinds(db);

    kindList.add(0, new Kind(allKindId, "Все виды"));

    SQLiteStatement statement = db.compileStatement(INSERT);
    for (Kind kind : kindList) {
      if (kindSelected == null && kind.getKindId() == 0) kindSelected = kind;
      statement.clearBindings();
      statement.bindLong(1, kind.getKindId());
      statement.bindString(2, kind.getKindName());
      statement.bindLong(3, 0L);
      statement.executeInsert();
    }

    if (kindSelected == null) kindSelected = kindList.get(0);
    setSelectedKind(kindSelected, db);
  }

  @NonNull
  public List<Kind> getKindList() {
    List<Kind> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      cursor = query(db, TABLE_NAME);
      while (cursor.moveToNext()) {
        Kind kind = getKind(cursor);
        result.add(kind);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }

  @Nullable
  public Kind getSelectedKind() {
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      return getSelectedKind(db);
    } catch (Exception ex) {
      log(ex);
    }
    return null;
  }

  @Nullable
  Kind getSelectedKind(SQLiteDatabase db) {
    Cursor cursor = null;
    try {
      cursor = query(db, TABLE_NAME, SELECTED + " = 1");
      if (cursor.moveToNext()) {
        return getKind(cursor);
      }
    } finally {
      close(cursor);
    }
    return null;
  }

  /*public void deleteKinds() {
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
       db.delete(TABLE_NAME, null, null);
    } catch (Exception ex) {
      log(ex);
    }
  }*/

  void deleteKinds(SQLiteDatabase db) {
    db.delete(TABLE_NAME, null, null);
  }

  public void setSelectedKind(Kind kind) {
    if (kind == null) return;
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      setSelectedKind(kind, db);
    } catch (Exception ex) {
      log(ex);
    }
  }

  void setSelectedKind(Kind kind, SQLiteDatabase db) {
    if (kind == null) return;
    ContentValues values = new ContentValues();
    values.put(SELECTED, 0);
    db.update(TABLE_NAME, values, null, null);
    values.put(SELECTED, 1);
    db.update(TABLE_NAME, values, KIND_ID + "=" + kind.getKindId(), null);
  }

  private Kind getKind(Cursor cursor) {
    return new Kind(
        getLong(cursor, KIND_ID),
        getString(cursor, KIND_NAME));
  }
}
