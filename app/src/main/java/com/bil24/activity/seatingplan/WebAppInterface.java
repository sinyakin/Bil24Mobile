package com.bil24.activity.seatingplan;

import android.support.v7.app.AppCompatActivity;
import android.webkit.*;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.Settings;
import server.net.*;
import server.net.listener.ReservationListener;
import server.net.obj.*;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("WeakerAccess")
class WebAppInterface {
  private AtomicInteger countTicket;
  private AppCompatActivity compatActivity;
  private com.rey.material.widget.Button buttonReserve;
  private com.rey.material.widget.Button buttonClear;
  private WebView webView;
  private long actionId;

  public WebAppInterface(long actionId, AtomicInteger countTicket, AppCompatActivity compatActivity, com.rey.material.widget.Button buttonReserve, com.rey.material.widget.Button buttonClear, WebView webView) {
    this.actionId = actionId;
    this.countTicket = countTicket;
    this.compatActivity = compatActivity;
    this.buttonReserve = buttonReserve;
    this.buttonClear = buttonClear;
    this.webView = webView;
  }

  @JavascriptInterface
  public void sbtOwner(String sbtOwner) {
    try {
      Integer integer = Integer.parseInt(sbtOwner);
      countTicket.set(integer);
      buttonReserve.setEnabled(true);
      buttonClear.setEnabled(true);
    } catch (Exception ignored) {}
  }

  @JavascriptInterface
  public void showToastLong(String text) {
    Dialogs.toastLong(text);
  }

  //нельзя делать private - не будет работать java script
  @SuppressWarnings("WeakerAccess")
  @JavascriptInterface
  public void showToastShort(String text) {
    Dialogs.toastShort(text);
  }

  @JavascriptInterface
  public void reserve(String seatId_, String state_, String elementId) {
    try {
      long seatId = Long.parseLong(seatId_);
      int state = Integer.parseInt(state_);

      if (state == 1) {
        //разбронирование
        NetManager.unReserve(new Reservation(elementId), Collections.singletonList(seatId));
      } else {
        //бронирование
        NetManager.reserveByPlace(new Reservation(elementId), Collections.singletonList(seatId), Settings.getActionIdKdp(actionId));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void ok(String elementId) {
    webView.loadUrl("javascript:reserveOk(\"" + elementId + "\")");
  }

  private void fail(String elementId) {
    webView.loadUrl("javascript:reserveFail(\"" + elementId + "\")");
  }

  class Reservation implements ReservationListener {

    String elementId;

    public Reservation(String elementId) {
      this.elementId = elementId;
    }

    @Override
    public void onReservation(ReservationClient clientData) {
      ok(elementId);
      if (clientData.getType() == ReservationServer.Type.RESERVE_BY_PLACE) {
        Settings.incSeatInReserve();
        countTicket.incrementAndGet();
      } else {
        Settings.decSeatInReserve();
        countTicket.decrementAndGet();
      }

      if (countTicket.get() > 0) {
        buttonReserve.setEnabled(true);
        buttonClear.setEnabled(true);
      }
      else {
        buttonReserve.setEnabled(false);
        buttonClear.setEnabled(false);
      }
    }

    @Override
    public void onReservationFailed(NetException e) {
      fail(elementId);
      if (e.isUserMessageConfirmEmail()) Dialogs.showEmailConfirmationDialog(compatActivity.getSupportFragmentManager(), e.getMessage());
      else if (e.isUserMessage()) showToastShort(e.getMessage());
    }
  }
}
