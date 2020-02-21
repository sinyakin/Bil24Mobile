package com.bil24.storage.sql;


import android.content.Context;
import android.database.sqlite.*;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bil24.storage.*;
import server.net.obj.extra.*;

import java.util.List;

public class DataBase extends SQLiteOpenHelper {

  private static final String NAME = SettingsCommon.isRealZone() ? "bil24Real.db" : "bil24.db";
  private static final int VERSION = 12;
  private static DataBase instance;

  public TableTicket ticket;
  public TableActionEvent action;
  public TableError error;
  private TableUserData userData;
  public TableMainData mainData;
  public TableCity city;
  public TableVenue venue;
  public TableKind kind;
  public TableMECInfo mecInfo;
  public TableMEC mec;

  private TableCommon tableCommon = new TableCommon();

  private DataBase(Context context) {
    super(context, NAME, null, VERSION);
    ticket = new TableTicket(this);
    action = new TableActionEvent(this);
    error = new TableError(this);
    userData = new TableUserData(this);
    mainData = new TableMainData(this);
    city = new TableCity(this);
    venue = new TableVenue(this);
    kind = new TableKind(this);
    mecInfo = new TableMECInfo(this);
    mec = new TableMEC(this);
  }

  public static DataBase getInstance(Context context) {
    if (instance == null) {
      instance = new DataBase(context.getApplicationContext());
    }
    return instance;
  }

  //метод вызывается если БД не создана
  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      db.beginTransaction();
      db.execSQL(action.createTable());
      db.execSQL(ticket.createTable());
      db.execSQL(error.createTable());
      db.execSQL(userData.createTable());
      db.execSQL(mainData.createTable());
      db.execSQL(city.createTable());
      db.execSQL(venue.createTable());
      db.execSQL(venue.createIndexCityId());
      db.execSQL(kind.createTable());
      db.execSQL(mecInfo.createTable());
      db.execSQL(mec.createTable());
      mainData.addDefaultData(db);
      db.setTransactionSuccessful();
    } catch (Exception ex) {
      Table.log(ex);
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion == 9) {
      try {
        db.beginTransaction();
        db.execSQL(mecInfo.createTable());
        db.execSQL(mec.createTable());
        db.setTransactionSuccessful();
        oldVersion = 10;
      } catch (Exception ex) {
        Table.log(ex);
      } finally {
        db.endTransaction();
      }
    }
    if (oldVersion == 10) {
      try {
        db.beginTransaction();
        db.execSQL(mainData.createTable());
        mainData.addDefaultData(db);
        Session session = userData.getSession(db);
        mainData.updateSession(db, session.getUserId(), session.getSessionId(), Settings.getEmail());
        FrontendData frontendData = Settings.getFrontendData();

        if (frontendData.getFid() == TableMainData.MAIN_FID) {
          mainData.setDefaultFrontendData(db, frontendData.getFid(), frontendData.getToken(), frontendData.getFrontendType());
        } else {
          mainData.setDefaultFrontendData(db, TableMainData.MAIN_FID, TableMainData.MAIN_TOKEN, TableMainData.MAIN_FRONTEND_TYPE);
          mainData.setNewFrontendData(db, frontendData.getFid(), frontendData.getToken(), frontendData.getFrontendType());
        }
        db.setTransactionSuccessful();
        oldVersion = 11;
      } catch (Exception ex) {
        Log.d("!!!!!", "error|" + ex.getMessage());
        Table.log(ex);
      } finally {
        db.endTransaction();
      }
    }
    if (oldVersion == 11) {
      try {
        db.beginTransaction();
        db.execSQL(mecInfo.dropTable());
        db.execSQL(mecInfo.createTable());
        db.setTransactionSuccessful();
        oldVersion = 12;
      } catch (Exception ex) {
        Table.log(ex);
      } finally {
        db.endTransaction();
      }
    }
  }

  @Override
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    super.onDowngrade(db, oldVersion, newVersion);
  }

  public boolean removeUserData() {
    return tableCommon.removeUserData(this);
  }

  @NonNull
  public static Session getSession(Context context) {
    return getInstance(context).mainData.getSession();
  }

  public static void clearSession(Context context) {
    getInstance(context).mainData.clearSession();
  }

  public void updateFilterData(List<ExtraCityFilter> cityFilterList, List<ExtraKindFilter> kindFilterList) {
    tableCommon.updateFilterData(cityFilterList, kindFilterList, this);
  }
}