package com.bil24.storage.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.annotation.*;
import com.bil24.fragments.action.filter.*;

import java.util.*;

/**
 * Виктор
 * 18.08.2015.
 */
public class TableVenue extends Table {
  public static final String TABLE_NAME = "venue";
  public static final String ID = "id";
  public static final String VENUE_ID = "venueId";
  public static final String VENUE_NAME = "venueName";
  public static final String CITY_ID = "cityId";
  public static final String CITY_NAME = "cityName";
  public static final String SELECTED = "selected";

  public static final String INSERT = "INSERT INTO " + TABLE_NAME + " (venueId, venueName, cityId, cityName, selected) VALUES (?, ?, ?, ?, ?)";

  public static final Long allVenueId = -1L;

  public TableVenue(@NonNull SQLiteOpenHelper helper) {
    super(helper);
  }

  String createTable() {
    return "CREATE TABLE " + TABLE_NAME + " (" +
        ID + " integer PRIMARY KEY AUTOINCREMENT, " +
        VENUE_ID + " bigint, " +
        VENUE_NAME + " text not null, " +
        CITY_ID + " bigint, " +
        CITY_NAME + " text not null, " +
        SELECTED + " integer)";
  }

  public void addVenueDefault(City city) {
    try {
      Venue venue = new Venue(allVenueId, "Все места", city.getCityId(), city.getCityName());
      ContentValues values = new ContentValues();
      values.put(VENUE_ID, venue.getVenueId());
      values.put(VENUE_NAME, venue.getVenueName());
      values.put(CITY_ID, venue.getCityId());
      values.put(CITY_NAME, venue.getCityName());
      values.put(SELECTED, 1);
      SQLiteDatabase db = helper.getWritableDatabase();
      db.insertOrThrow(TableVenue.TABLE_NAME, null, values);
    } catch (Exception ex) {
//      MyUncaughtExceptionHandler.sendError(ex);
      log(ex);
    }
  }

  void addVenues(@NonNull List<Venue> list, SQLiteDatabase db) {
    if (list.isEmpty()) return;
    Venue venueSelected = getSelectedVenue(db);
    if (venueSelected == null) venueSelected = list.get(0);
    clearVenues(db);

    SQLiteStatement statement = db.compileStatement(INSERT);

    for (Venue venue : list) {
      statement.clearBindings();
      statement.bindLong(1, venue.getVenueId());
      statement.bindString(2, venue.getVenueName());
      statement.bindLong(3, venue.getCityId());
      statement.bindString(4, venue.getCityName());
      statement.bindLong(4, 0L);
      statement.executeInsert();
    }
    setSelectedVenue(venueSelected, db);
  }

  @NonNull
  public List<Venue> getVenueListByCityId(long cityId) {
    List<Venue> result = new ArrayList<>();
    Venue defaultVenue = null;
    Cursor cursor = null;
    try {
      SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
      cursor = query(sqLiteDatabase, TABLE_NAME, CITY_ID + "=" + cityId);
      while (cursor.moveToNext()) {
        Venue venue = getVenue(cursor);
        if (venue.getVenueId() == allVenueId) {
          defaultVenue = venue;
          continue;
        }
        result.add(venue);
      }
    } catch (Exception ex) {
      log(ex);
    } finally {
      close(cursor);
    }
    Collections.sort(result, new Comparator<Venue>() {
      @Override
      public int compare(Venue venue1, Venue venue2) {
        return venue1.getVenueName().compareTo(venue2.getVenueName());
      }
    });
    if (defaultVenue != null) result.add(0, defaultVenue);
    return result;
  }

  @Nullable
  public Venue getSelectedVenue() {
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      return getSelectedVenue(db);
    } catch (Exception ex) {
      log(ex);
    }
    return null;
  }

  @Nullable
  Venue getSelectedVenue(SQLiteDatabase db) {
    Cursor cursor = null;
    try {
      cursor = query(db, TABLE_NAME, SELECTED + " = 1");
      if (cursor.moveToNext()) {
        return getVenue(cursor);
      }
    } finally {
      close(cursor);
    }
    return null;
  }

  /*public void clearVenues() {
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      db.delete(TABLE_NAME, null, null);
//      db.execSQL("VACUUM");
    } catch (Exception ex) {
      log(ex);
    }
  }
*/
  void clearVenues(SQLiteDatabase db) {
    db.delete(TABLE_NAME, "1", null);
  }

  String createIndexCityId() {
    return "CREATE INDEX IF NOT EXISTS cityId ON " + TABLE_NAME + " (cityId)";
  }

  @Nullable
  public Long getSelectedVenueId() {
    Venue venue = getSelectedVenue();
    if (venue == null) return null;
    if (venue.getVenueId() == allVenueId) return null;
    return venue.getVenueId();
  }

  public void setSelectedVenue(Venue venue) {
    if (venue == null) return;
    try {
      SQLiteDatabase db = helper.getReadableDatabase();
      setSelectedVenue(venue, db);
    } catch (Exception ex) {
      log(ex);
    }
  }

  public void setSelectedVenue(Venue venue, SQLiteDatabase db) {
    if (venue == null) return;
    ContentValues values = new ContentValues();
    values.put(SELECTED, 0);
    db.update(TABLE_NAME, values, null, null);
    values.put(SELECTED, 1);
    db.update(TABLE_NAME, values, VENUE_ID + "=" + venue.getVenueId(), null);
  }

  private Venue getVenue(Cursor cursor) {
    return new Venue(
        getLong(cursor, VENUE_ID),
        getString(cursor, VENUE_NAME),
        getLong(cursor, CITY_ID),
        getString(cursor, CITY_NAME));
  }
}
