package com.bil24.storage.sql;

import android.database.sqlite.SQLiteDatabase;
import com.bil24.fragments.action.filter.*;
import server.net.obj.extra.*;

import java.util.*;

/**
 * Created by SVV on 14.05.2016
 */
class TableCommon {

  /**
   * Метод обновляет данные фильтров в базе после запроса GET_ACTIONS
   */
  void updateFilterData(List<ExtraCityFilter> cityFilterList, List<ExtraKindFilter> kindFilterList, DataBase dataBase) {
    if (cityFilterList.isEmpty() || kindFilterList.isEmpty()) return;

    //подготовим данные
    List<City> cityList = new ArrayList<>();
    List<Venue> venueList = new ArrayList<>();
    for (ExtraCityFilter cityFilter : cityFilterList) {
      cityList.add(new City(cityFilter.getCityId(), cityFilter.getCityName()));
      venueList.add(new Venue(TableVenue.allVenueId, "Все места", cityFilter.getCityId(), cityFilter.getCityName()));
      for (ExtraVenueFilter venueFilter : cityFilter.getVenueList()) {
        venueList.add(new Venue(venueFilter.getVenueId(), venueFilter.getVenueName(), cityFilter.getCityId(), cityFilter.getCityName()));
      }
    }
    List<Kind> kindList = new ArrayList<>();
    for (ExtraKindFilter kindFilter : kindFilterList) {
      kindList.add(new Kind(kindFilter.getKindId(), kindFilter.getKindName()));
    }

    //запишем данные в базу
    SQLiteDatabase db = null;
    try {
      db = dataBase.getWritableDatabase();
      db.beginTransaction();
      dataBase.venue.addVenues(venueList, db);
      dataBase.city.addCities(cityList, db);
      dataBase.kind.addKinds(kindList, db);
      db.setTransactionSuccessful();
    } catch (Exception ex) {
      Table.log(ex);
    } finally {
      if (db != null) db.endTransaction();
    }
  }

  boolean removeUserData(DataBase dataBase) {
    SQLiteDatabase db = null;
    try {
      db = dataBase.getWritableDatabase();
      db.beginTransaction();
      dataBase.action.clearTable(db);
      dataBase.ticket.clearTable(db);
      dataBase.mecInfo.clearTable(db);
      dataBase.mec.clearTable(db);
      db.setTransactionSuccessful();
    } catch (Exception ex) {
      Table.log(ex);
      return false;
    } finally {
      if (db != null) db.endTransaction();
    }
    return true;
  }
}
