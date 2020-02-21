package com.bil24.storage;

import android.content.*;
import android.support.annotation.Nullable;
import com.bil24.Bil24Application;

/**
 * Created by SVV on 29.09.2015
 */
public class SettingsCommon {
  private static String PREF_COMMON_SETTINGS = "BIL24COMMON.CFG";
  private static final String ZONE = "ZONE";
  private static final String NOTIFICATION_ID = "NOTIFICATION_ID";
  private static final String NOTIFICATION_NEWS = "NOTIFICATION_NEWS_V2";
  private static final String VISIBLE_SERVICE_INFO = "VISIBLE_SERVICE_INFO";
  private static final String NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL";

  private static SharedPreferences getShared() {
    return Bil24Application.getContext().getSharedPreferences(PREF_COMMON_SETTINGS, Context.MODE_PRIVATE);
  }

  public static void setZone(boolean real) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(ZONE, real);
    editor.apply();
  }

  public static boolean isVisibleServiceInfo() {
    return getShared().getBoolean(VISIBLE_SERVICE_INFO, false);
  }

  public static void setVisibleServiceInfo(boolean visibleServiceInfo) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(VISIBLE_SERVICE_INFO, visibleServiceInfo);
    editor.apply();
  }

  public static boolean isCreatedChannel() {
    return getShared().getBoolean(NOTIFICATION_CHANNEL, false);
  }

  public static void createChannel() {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(NOTIFICATION_CHANNEL, true);
    editor.apply();
  }

  public static boolean isRealZone() {
    return getShared().getBoolean(ZONE, true);
  }

  //--------------------------------------------------
  public static int incNotificationId() {
    int nId = getNotificationId() + 1;
    setNotificationId(nId);
    return nId;
  }

  private static void setNotificationId(int notificationId) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putInt(NOTIFICATION_ID, notificationId);
    editor.apply();
  }

  private static int getNotificationId() {
    return getShared().getInt(NOTIFICATION_ID, 0);
  }
  //--------------------------------------------------

  //------------------------------------------------------------
  //настройки уведомлений
  public static void setNotificationNews(boolean enable, long fid) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putString(NOTIFICATION_NEWS + "_" + fid, String.valueOf(enable));
    editor.apply();
  }

  @Nullable
  public static Boolean isNotificationNews(long fid) {
    String res = getShared().getString(NOTIFICATION_NEWS + "_" + fid, null);
    if (res == null) return null;
    return Boolean.parseBoolean(res);
  }
}
