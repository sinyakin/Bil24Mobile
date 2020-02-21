package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.*;
import com.bil24.fragments.action.filter.City;

import java.util.*;

/**
 * Виктор
 * 18.08.2015.
 */
public class TableCity extends Table {

  private static final class Cols {
    public static final String TABLE_NAME = "city";
    public static final String CITY_ID = "cityId";
    public static final String CITY_NAME = "cityName";
    public static final String SELECTED = "selected";
  }

  public static final String INSERT = "INSERT INTO " + Cols.TABLE_NAME + " (cityId, cityName, selected) VALUES (?, ?, ?)";

  public TableCity(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + Cols.TABLE_NAME + " (" +
        Cols.CITY_ID + " bigint PRIMARY KEY, " +
        Cols.CITY_NAME + " text not null, " +
        Cols.SELECTED + " integer)";
  }

  /*public void updateCity(@NonNull City city) {
    City dbCity = getCityById(city.getCityId());
    if (dbCity == null) addCity(city);
    else updateCityName(city);
  }

  private void addCity(@NonNull City city) {
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.CITY_ID, city.getCityId());
      values.put(Cols.CITY_NAME, city.getCityName());
      values.put(Cols.SELECTED, 0);
      SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
      sqLiteDatabase.insertOrThrow(TableCity.Cols.TABLE_NAME, null, values);
    } catch (Exception ex) {
      log(ex);
    }
  }*/

  public void addCities(@NonNull List<City> list) {
    SQLiteDatabase db = null;
    try {
      db = helper.getWritableDatabase();
      db.beginTransaction();
      addCities(list, db);
      db.setTransactionSuccessful();
    } catch (Exception ex) {
//      MyUncaughtExceptionHandler.sendError(ex);
      Table.log(ex);
    } finally {
      if (db != null) db.endTransaction();
    }
  }

  void addCities(@NonNull List<City> list, SQLiteDatabase db) {
    if (list.isEmpty()) return;
    City citySelected = getSelectedCity(db);
    if (citySelected == null) citySelected = list.get(0);
    else if (!list.contains(citySelected)) citySelected = list.get(0);
    clearCities(db);

    SQLiteStatement statement = db.compileStatement(INSERT);
    for (City city : list) {
      statement.clearBindings();
      statement.bindLong(1, city.getCityId());
      statement.bindString(2, city.getCityName());
      statement.bindLong(3, 0L);
      statement.executeInsert();
    }

    setSelectedCity(citySelected, db);
  }

 /* private void updateCityName(@NonNull City city) {
    try {
      ContentValues values = new ContentValues();
      values.put(Cols.CITY_NAME, city.getCityName());
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.update(Cols.TABLE_NAME, values, Cols.CITY_ID + "=" + city.getCityId(), null);
    } catch (Exception ex) {
      log(ex);
    }
  }*/

  /*@Nullable
  public City getCityById(Long cityId) {
    City result = null;
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, Cols.TABLE_NAME, Cols.CITY_ID + " = " + cityId);
      if (cursor.moveToNext()) {
        result = getCity(cursor);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    return result;
  }*/

  @NonNull
  public List<City> getCitiesList() {
    List<City> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, Cols.TABLE_NAME);
      while (cursor.moveToNext()) {
        City pass = getCity(cursor);
        result.add(pass);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    Collections.sort(result, new Comparator<City>() {
      @Override
      public int compare(City city1, City city2) {
        return city1.getCityName().compareTo(city2.getCityName());
      }
    });
    return result;
  }

  @Nullable
  public City getSelectedCity() {
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      return getSelectedCity(db);
    } catch (Exception ex) {
      log(ex);
    }
    return null;
  }

  @Nullable
  private City getSelectedCity(SQLiteDatabase db) {
    Cursor cursor = null;
    try {
      cursor = query(db, Cols.TABLE_NAME, Cols.SELECTED + " = 1");
      if (cursor.moveToNext()) {
        return getCity(cursor);
      }
    } finally {
      close(cursor);
    }
    return null;
  }

  public long getSelectedCityId() {
    City city = getSelectedCity();
    return city == null ? -1 : city.getCityId();
  }

  /*public void clearCities() {
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      sqLiteDatabase.delete(Cols.TABLE_NAME, null, null);
    } catch (Exception ex) {
      log(ex);
    }
  }*/

  void clearCities(SQLiteDatabase db) {
    db.delete(Cols.TABLE_NAME, "1", null);
  }

  public void setSelectedCity(City city) {
    if (city == null) return;
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      setSelectedCity(city, db);
    } catch (Exception ex) {
      log(ex);
    }
  }

  void setSelectedCity(City city, SQLiteDatabase db) {
    if (city == null) return;
    ContentValues values = new ContentValues();
    values.put(Cols.SELECTED, 0);
    db.update(Cols.TABLE_NAME, values, null, null);
    values.put(Cols.SELECTED, 1);
    db.update(Cols.TABLE_NAME, values, Cols.CITY_ID + "=" + city.getCityId(), null);
  }

  private City getCity(Cursor cursor) {
    return new City(
        getLong(cursor, Cols.CITY_ID),
        getString(cursor, Cols.CITY_NAME));
  }
}
