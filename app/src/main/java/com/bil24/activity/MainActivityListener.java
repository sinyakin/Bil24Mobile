package com.bil24.activity;

import android.support.annotation.NonNull;
import server.net.obj.extra.ExtraVenue;

/**
 * User: SVV
 * Date: 04.06.2015.
 */
public interface MainActivityListener {
  void showActionPageFragment();
  void showBasketFragment();
  void showOrderFragment();
  void showTicket1Fragment(int tryingGetTickets);
  void showTicket2Fragment(long actionEventId, String actionName);
  void showMec1Fragment();
  void showMec2Fragment(long actionEventId, long cityId, String actionName, String venueName, boolean cityPass);
  void showPromoCodesFragment();
  void showEmptyFragment();
  void showNewsFragment();
  void showSettingFragment();
  void showAgreementFragment();
  void showCityPassFragment(long cityId);
  void showCityPassVenueFragment(@NonNull ExtraVenue venue);
  void showAlfaActivity(Long orderId, String url, boolean openMECFragmentAfterPaid);
  @Deprecated
  void showTicketActivity(long actionEventId, String actionName);
  @Deprecated
  void showMECActivity(long actionEventId, String actionName, String venueName);
  void showSeatingPlanActivity(String placementUrl, long actionId, String actionName, Long actionEventId, String actionEventTime);
  void showImageActivity(String url);
  void showCityActivity();
  void processWithGCM();
}
