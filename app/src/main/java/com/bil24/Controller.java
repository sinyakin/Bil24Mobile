package com.bil24;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bil24.activity.*;
import com.bil24.adapter.PagerListener;
import com.bil24.fragments.*;
import com.bil24.fragments.action.listener.*;
import com.bil24.menu.UpdateLeftMenuListener;
import com.bil24.service.ServiceConnectorListener;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;

import server.net.*;
import server.net.listener.GetUserInfoListener;
import server.net.obj.GetUserInfoClient;
import server.net.obj.extra.*;

/**
 * User: SVV
 * Date: 02.06.2015.
 */
public class Controller implements MainActivityListener, PagerListener, ActionUpdateListener,
    UpdateLeftMenuListener, UpdateTopMenuListener, FilterListener,
    ServiceConnectorListener, BasketFragmentFullNameListener, SettingFragmentListener {

  private PagerListener pagerListener;
  private MainActivityListener mainActivityListener;
  private ActionUpdateListener actionUpdateListener;
  private UpdateLeftMenuListener updateLeftMenuListener;
  private UpdateTopMenuListener updateTopMenuListener;
  private FilterListener filterListener;
  private ServiceConnectorListener serviceConnectorListener;
  private BasketFragmentFullNameListener basketFragmentFullNameListener;
  private SettingFragmentListener settingFragmentListener;
  private FrontendData frontendData;
  private boolean kassatka = false;

  private boolean checkGcm = true;

  private static Controller instance;

  public static Controller getInstance() {
    if (instance == null) {
//      Settings.removeAllData();
      instance = new Controller();
    }
    return instance;
  }

  public boolean isKassatka() {
    return kassatka;
  }

  public void setKassatka(String model) {
    this.kassatka = model.toLowerCase().equals("tps900");
  }

  public boolean changeFrontendTypeInKassatka(Context context) {
    return isKassatka() && getFrontendData(context).getFrontendType() != FrontendType.TICKET_OFFICE;
  }

  public boolean canWorkAsPrintTerminal(Context context) {
    return isKassatka() && getFrontendData(context).getFrontendType() == FrontendType.TICKET_OFFICE;
  }

  public void setSettingFragmentListener(SettingFragmentListener settingFragmentListener) {
    this.settingFragmentListener = settingFragmentListener;
  }

  public void setBasketFragmentFullNameListener(BasketFragmentFullNameListener basketFragmentFullNameListener) {
    this.basketFragmentFullNameListener = basketFragmentFullNameListener;
  }

  public synchronized FrontendData getFrontendData(Context context) {
    if (frontendData == null) frontendData = DataBase.getInstance(context).mainData.getFrontendData();
    return frontendData;
  }

  public synchronized void clearFrontendData() {
    frontendData = null;
  }

  public boolean isDefaultFrontend(Context context) {
    return !getFrontendData(context).isNewFrontend();
  }

  /*public synchronized void updateFrontendData(FrontendData frontendData) {
    Settings.setFrontendData(frontendData.getFid(), frontendData.getToken(), frontendData.getFrontendType());
    this.frontendData = frontendData;
  }*/

  private boolean isCheckGcm() {
    return checkGcm;
  }

  public void setCheckGcm(boolean checkGcm) {
    this.checkGcm = checkGcm;
  }

  public void setServiceConnectorListener(ServiceConnectorListener serviceConnectorListener) {
    this.serviceConnectorListener = serviceConnectorListener;
  }

  public void setFilterListener(FilterListener filterListener) {
    this.filterListener = filterListener;
  }

  public void setUpdateLeftMenuListener(UpdateLeftMenuListener updateLeftMenuListener) {
    this.updateLeftMenuListener = updateLeftMenuListener;
  }

  public void setUpdateTopMenuListener(UpdateTopMenuListener updateTopMenuListener) {
    this.updateTopMenuListener = updateTopMenuListener;
  }

  public void setActionUpdateListener(ActionUpdateListener actionUpdateListener) {
    this.actionUpdateListener = actionUpdateListener;
  }

  public void setPagerListener(PagerListener pagerListener) {
    this.pagerListener = pagerListener;
  }

  public void setMainActivityListener(MainActivityListener mainActivityListener) {
    this.mainActivityListener = mainActivityListener;
  }

  @Override
  public void showActionPageFragment() {
    if (mainActivityListener != null) mainActivityListener.showActionPageFragment();
  }

  @Override
  public void showBasketFragment() {
    if (mainActivityListener != null) mainActivityListener.showBasketFragment();
  }

  @Override
  public void showOrderFragment() {
    if (mainActivityListener != null) mainActivityListener.showOrderFragment();
  }

  @Override
  public void showTicket1Fragment(int tryingGetTickets) {
    if (mainActivityListener != null) mainActivityListener.showTicket1Fragment(tryingGetTickets);
  }

  @Override
  public void showTicket2Fragment(long actionEventId, String actionName) {
    if (mainActivityListener != null) mainActivityListener.showTicket2Fragment(actionEventId, actionName);
  }

  @Override
  public void showMec1Fragment() {
    if (mainActivityListener != null) mainActivityListener.showMec1Fragment();
  }

  @Override
  public void showMec2Fragment(long actionEventId, long cityId, String actionName, String venueName, boolean cityPass) {
    if (mainActivityListener != null) mainActivityListener.showMec2Fragment(actionEventId, cityId, actionName, venueName, cityPass);
  }

  @Override
  public void showPromoCodesFragment() {
    if (mainActivityListener != null) mainActivityListener.showPromoCodesFragment();
  }

  @Override
  public void showEmptyFragment() {
    if (mainActivityListener != null) mainActivityListener.showEmptyFragment();
  }

  @Override
  public void showNewsFragment() {
    if (mainActivityListener != null) mainActivityListener.showNewsFragment();
  }

  @Override
  public void showSettingFragment() {
    if (mainActivityListener != null) mainActivityListener.showSettingFragment();
  }

  @Override
  public void showAgreementFragment() {
    if (mainActivityListener != null) mainActivityListener.showAgreementFragment();
  }

  @Override
  public void showCityPassFragment(long cityId) {
    if (mainActivityListener != null) mainActivityListener.showCityPassFragment(cityId);
  }

  @Override
  public void showCityPassVenueFragment(@NonNull ExtraVenue venue) {
    if (mainActivityListener != null) mainActivityListener.showCityPassVenueFragment(venue);
  }

  @Override
  public void showAlfaActivity(Long orderId, String url, boolean openMECFragmentAfterPaid) {
    if (mainActivityListener != null) mainActivityListener.showAlfaActivity(orderId, url, openMECFragmentAfterPaid);
  }

  @Deprecated
  @Override
  public void showTicketActivity(long actionEventId, String actionName) {
    if (mainActivityListener != null) mainActivityListener.showTicketActivity(actionEventId, actionName);
  }

  @Deprecated
  @Override
  public void showMECActivity(long actionEventId, String actionName, String venueName) {
    if (mainActivityListener != null) mainActivityListener.showMECActivity(actionEventId, actionName, venueName);
  }

  @Override
  public void showSeatingPlanActivity(String placementUrl, long actionId, String actionName, Long actionEventId, String actionEventTime) {
    if (mainActivityListener != null) mainActivityListener.showSeatingPlanActivity(placementUrl, actionId, actionName, actionEventId, actionEventTime);
  }

  @Override
  public void showImageActivity(String url) {
    if (mainActivityListener != null) mainActivityListener.showImageActivity(url);
  }

  @Override
  public void showCityActivity() {
    if (mainActivityListener != null) mainActivityListener.showCityActivity();
  }

  @Override
  public void processWithGCM() {
    if (mainActivityListener != null && isCheckGcm()) mainActivityListener.processWithGCM();
  }

  @Override
  public void openPage1(boolean deleteTwoPage) {
    if (pagerListener != null) pagerListener.openPage1(deleteTwoPage);
  }

  @Override
  public void openPage2(ExtraActionV2 actionRoot) {
    if (pagerListener != null) pagerListener.openPage2(actionRoot);
  }

  @Override
  public int getPage() {
    return pagerListener.getPage();
  }

  public boolean openTwoPage() {
    return (pagerListener.getPage() == 1);
  }

  @Override
  public void actionUpdate(boolean changeCity) {
    if (actionUpdateListener != null) actionUpdateListener.actionUpdate(changeCity);
  }

  @Override
  public void actionDelete(long actionId) {
    if (actionUpdateListener != null) actionUpdateListener.actionDelete(actionId);
  }

  public void getUserInfo() {
      NetManager.getUserInfo(new GetUserInfoListener() {
      @Override
      public void onGetUserInfo(GetUserInfoClient clientData) {
        Settings.setSeatInReserve(clientData.getSeatInReserve());
        Settings.setOrderInWait(clientData.getOrderInWait());
      }

      @Override
      public void onGetUserInfoFailed(NetException e) {}
    });
  }

  @Override
  public void updateLeftMenuBasketAmount(int amount) {
    if (updateLeftMenuListener != null) updateLeftMenuListener.updateLeftMenuBasketAmount(amount);
  }

  @Override
  public void updateLeftMenuTicketAmount() {
    if (updateLeftMenuListener != null) updateLeftMenuListener.updateLeftMenuTicketAmount();
  }

  @Override
  public void updateLeftMenuMECsAmount() {
    if (updateLeftMenuListener != null) updateLeftMenuListener.updateLeftMenuMECsAmount();
  }

  @Override
  public void updateLeftMenuOrderAmount(int amount) {
    if (updateLeftMenuListener != null) updateLeftMenuListener.updateLeftMenuOrderAmount(amount);
  }

  @Override
  public void updateLeftMenuPromoCodesAmount(int amount) {
    if (updateLeftMenuListener != null) updateLeftMenuListener.updateLeftMenuPromoCodesAmount(amount);
  }

  @Override
  public void resetLeftMenu() {
    if (updateLeftMenuListener != null) updateLeftMenuListener.resetLeftMenu();
  }

  @Override
  public void updateTicketTopMenu(String text) {
    if (updateTopMenuListener != null) updateTopMenuListener.updateTicketTopMenu(text);
  }

  @Override
  public void actionFilter(String text) {
    if (filterListener != null) filterListener.actionFilter(text);
  }

  @Override
  public void setFilterText(String text) {
    if (filterListener != null) filterListener.setFilterText(text);
  }

  @Override
  public void sendToServiceWakeup() {
    if (serviceConnectorListener != null) serviceConnectorListener.sendToServiceWakeup();
  }

  @Override
  public void onSuccessFullName(@NonNull String fullName) {
    if (basketFragmentFullNameListener != null) basketFragmentFullNameListener.onSuccessFullName(fullName);
  }

  @Override
  public void changeEmail(@NonNull String email) {
    if (settingFragmentListener != null) settingFragmentListener.changeEmail(email);
  }
}
