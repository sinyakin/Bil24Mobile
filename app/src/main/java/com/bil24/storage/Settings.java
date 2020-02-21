package com.bil24.storage;

import android.content.*;
import android.support.annotation.*;
import com.bil24.*;
import com.bil24.storage.sql.TableMainData;
import com.bil24.utils.Utils;

import java.util.Date;

/**
 * User: SVV
 * Date: 17.06.2015.
 */
@SuppressWarnings("unchecked")
public class Settings {
  private static String PREF_FILE_CONFIG = "BIL24REAL.CFG";

  private static final String SEAT_IN_RESERVE = "SEAT_IN_RESERVE";
  private static final String ORDER_IN_WAIT = "ORDER_IN_WAIT";
  private static final String PROMO_CODES = "PROMO_CODES";
  private static final String ACTION_EVENT_TICKETS = "ACTION_EVENT_TICKETS";
  private static final String MEC_INFO_SIZE = "MEC_INFO_SIZE";
  private static final String MEC_ID = "MEC_ID";

  private static final String ORDER_ID = "ORDER_ID";
  private static final String EMAIL = "EMAIL";
  private static final String NEED_SEND_TO_EMAIL = "NEED_SEND_TO_EMAIL";

  private static final String SENT_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER_V2";
  private static final String PUSH_TOKEN = "PUSH_TOKEN";

  private static final String READ_NEWS_ID = "READ_NEWS_ID";
  private static final String ACTION_ID_KDP = "ACTION_ID_KDP";

  private static final String FILTER_DATE_FROM = "FILTER_DATE_FROM";

  @Deprecated
  private static final String FID = "FID";
  private static final String TOKEN = "TOKEN";
  private static final String FRONTEND_TYPE = "FRONTEND_TYPE";
  private static final String NEED_AUTH = "NEED_AUTH";

  private static final String WAIT_REFERRER = "WAIT_REFERRER";

  private static final String LAST_USER_ID = "LAST_USER_ID";
  private static final String LAST_SESSION_ID = "LAST_SESSION_ID";
  private static final String LAST_EMAIL = "LAST_EMAIL";
  @Deprecated
  private static final String SEAT_CART = "SEAT_CART";

  private static final String LAST_FULL_NAME = "LAST_FULL_NAME";

  static {
    PREF_FILE_CONFIG = SettingsCommon.isRealZone() ? "BIL24REAL.CFG" : "BIL24.CFG";
  }

  private static SharedPreferences getShared() {
    return Bil24Application.getContext().getSharedPreferences(PREF_FILE_CONFIG, Context.MODE_PRIVATE);
  }

  public static void removeAllData() {
    SharedPreferences.Editor editor = getShared().edit();
    editor.clear();
    editor.apply();
  }

  /**
   * Локальная настройка. Добавляем ордер, чтобы не показывать больше кнопку оплатить
   */
  public static void setOrderId(Long orderId) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putLong(ORDER_ID + orderId, orderId);
    editor.apply();
  }

  //------------------------------------------------------
  public static void setSeatInReserve(int seatInReserve) {
    SharedPreferences.Editor editor = getShared().edit();
    if (seatInReserve < 0) seatInReserve = 0;
    editor.putInt(SEAT_IN_RESERVE, seatInReserve);
    editor.apply();
    Controller.getInstance().updateLeftMenuBasketAmount(seatInReserve);
  }

  public static void addSeatInReserve(int amount) {
    int seatInReserve = getSeatInReserve();
    setSeatInReserve(seatInReserve + amount);
  }

  public static void incSeatInReserve() {
    int seatInReserve = getSeatInReserve();
    setSeatInReserve(++seatInReserve);
  }

  public static void decSeatInReserve() {
    int seatInReserve = getSeatInReserve();
    setSeatInReserve(--seatInReserve);
  }

  public static int getSeatInReserve() {
    return getShared().getInt(SEAT_IN_RESERVE, 0);
  }

  //---------------------------------------------
  public static void setOrderInWait(int quantity) {
    int oldQuantity = getActionEventTickets();
    if (oldQuantity == quantity) return;
    SharedPreferences.Editor editor = getShared().edit();
    if (quantity < 0) quantity = 0;
    editor.putInt(ORDER_IN_WAIT, quantity);
    editor.apply();
    Controller.getInstance().updateLeftMenuOrderAmount(quantity);
  }

  public static int getWaitInReserve() {
    return getShared().getInt(ORDER_IN_WAIT, 0);
  }

  //---------------------------------------------
  public static void setPromoCodes(int quantity) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putInt(PROMO_CODES, quantity);
    editor.apply();
    Controller.getInstance().updateLeftMenuPromoCodesAmount(quantity);
  }

  public static int getPromoCodes() {
    return getShared().getInt(PROMO_CODES, 0);
  }

  //---------------------------------------------
  public static void setActionEventTickets(int quantity) {
    int oldQuantity = getActionEventTickets();
    if (oldQuantity == quantity) return;
    SharedPreferences.Editor editor = getShared().edit();
    if (quantity < 0) quantity = 0;
    editor.putInt(ACTION_EVENT_TICKETS, quantity);
    editor.apply();
    Controller.getInstance().updateLeftMenuTicketAmount();
  }

  public static int getActionEventTickets() {
    return getShared().getInt(ACTION_EVENT_TICKETS, 0);
  }

  //---------------------------------------------
  public static void setMECInfoSize(int quantity) {
    int oldQuantity = getMECSInfoSize();
    if (oldQuantity == quantity) return;
    SharedPreferences.Editor editor = getShared().edit();
    if (quantity < 0) quantity = 0;
    editor.putInt(MEC_INFO_SIZE, quantity);
    editor.apply();
    Controller.getInstance().updateLeftMenuMECsAmount();
  }

  public static int getMECSInfoSize() {
    return getShared().getInt(MEC_INFO_SIZE, 0);
  }
  //---------------------------------------------

  public static void setMECId(long ticketId) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putLong(MEC_ID, ticketId);
    editor.apply();
  }

  public static long getMECId() {
    return getShared().getLong(MEC_ID, 0);
  }
  //---------------------------------------------

  public static boolean needPayment(Long orderId) {
    return getShared().getLong(ORDER_ID + orderId, -1) == -1;
  }

  @Deprecated //todo удалить когда версия приложения у всех будет > 28
  @Nullable
  public static String getEmail() {
    return getShared().getString(EMAIL, "");
  }

  public static boolean needSentToEmail() {
    return getShared().getBoolean(NEED_SEND_TO_EMAIL, false);
  }

  public static void setNeedSentToEmail(boolean needSentToEmail) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(NEED_SEND_TO_EMAIL, needSentToEmail);
    editor.apply();
  }

  //-----------------------------------------------------------
  //найстройки, связанные с GCM
  public static void sentTokenToServer(boolean sentTokenToServer) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(SENT_TOKEN_TO_SERVER, sentTokenToServer);
    editor.apply();
  }

  public static boolean isSentTokenToServer() {
    return getShared().getBoolean(SENT_TOKEN_TO_SERVER, false);
  }

  public static void setPushToken(String token) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putString(PUSH_TOKEN, token);
    editor.apply();
  }

  @Nullable
  public static String getPushToken() {
    return getShared().getString(PUSH_TOKEN, null);
  }

  //------------------------------------------------------------
  public static void setReadNewsId(long newsId) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(READ_NEWS_ID + "_" + newsId, true);
    editor.apply();
  }

  public static boolean existNewsId(long newsId) {
    return getShared().getBoolean(READ_NEWS_ID + "_" + newsId, false);
  }

  //------------------------------------------------------------
  public static void setFilterDateFrom(@NonNull String dateFrom) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putString(FILTER_DATE_FROM, dateFrom);
    editor.apply();
  }

  @NonNull
  public static String getFilterDateFrom() {
    String date = getShared().getString(FILTER_DATE_FROM, null);
    if (date == null) {
      date = Utils.dateFormatDDMMYYYY(new Date());
      setFilterDateFrom(date);
    } else {
      //todo заменить на проверку серверного времени
      long time = Utils.dateFormatDDMMYYYY(date).getTime();
      if (time < System.currentTimeMillis()) {
        date = Utils.dateFormatDDMMYYYY(new Date());
        setFilterDateFrom(date);
      }
    }
    return date;
  }

  //-------------------------------------------------------------
  public static void setActionIdKdp(long actionId, int kdp) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putInt(ACTION_ID_KDP + "_" + actionId, kdp);
    editor.apply();
  }

  public static void resetActionIdKdp(long actionId) {
    setActionIdKdp(actionId, -1);
  }

  public static boolean existKdp(long actionId) {
    Integer kdp = getActionIdKdp(actionId);
    return kdp != null;
  }

  @Nullable
  public static Integer getActionIdKdp(long actionId) {
    int kdp = getShared().getInt(ACTION_ID_KDP + "_" + actionId, -1);
    if (kdp == -1) return null;
    else return kdp;
  }
  //------------------------------------------------------------
  public static void setLastUserData(@NonNull LastUserData lastUserData) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putLong(LAST_USER_ID, lastUserData.getUserId());
    editor.putString(LAST_SESSION_ID, lastUserData.getSessionId());
    editor.putString(LAST_EMAIL, lastUserData.getEmail());
    editor.apply();
  }

  @NonNull
  public static LastUserData getLastUserData() {
    long userId = getShared().getLong(LAST_USER_ID, 0);
    String sessionId = getShared().getString(LAST_SESSION_ID, "");
    String email = getShared().getString(LAST_EMAIL, "");
    return new LastUserData(userId, sessionId, email);
  }

  //------------------------------------------------------------
  @Deprecated
  public static FrontendData getFrontendData() {
    long fid = getShared().getLong(FID, TableMainData.MAIN_FID);
    String token = getShared().getString(TOKEN, TableMainData.MAIN_TOKEN);
    int frontendType = getShared().getInt(FRONTEND_TYPE, TableMainData.MAIN_FRONTEND_TYPE.getId());
    return new FrontendData(fid, token, FrontendType.get(frontendType), Utils.DEFAULT_OBJECT_ID, "", FrontendType.UNKNOWN);
  }

  public static boolean isNeedAuth(Context context) {
    return Controller.getInstance().getFrontendData(context).getFrontendType() != FrontendType.ANDROID
        && getShared().getBoolean(NEED_AUTH, true);
  }

  public static void setNeedAuth(boolean needAuth) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(NEED_AUTH, needAuth);
    editor.apply();
  }

  public static boolean isWaitReferrer() {
    boolean result = getShared().getBoolean(WAIT_REFERRER, true);
    if (result) setWaitReferrer(false);
    return result;
  }

  private static void setWaitReferrer(boolean waitReferrer) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(WAIT_REFERRER, waitReferrer);
    editor.apply();
  }
  //------------------------------------------------------------
  /*public static void setSeatCart(long seatId, boolean res) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putBoolean(SEAT_CART + "_" + seatId, res);
    editor.apply();
  }

  public static boolean isSeatCart(long seatId) {
    return getShared().getBoolean(SEAT_CART + "_" + seatId, true);
  }*/

  //-----------------------------------------
  @NonNull
  public static String getLastFullName() {
    return getShared().getString(LAST_FULL_NAME, "");
  }

  public static void setLastFullName(@NonNull String fullName) {
    SharedPreferences.Editor editor = getShared().edit();
    editor.putString(LAST_FULL_NAME, fullName);
    editor.apply();
  }
}