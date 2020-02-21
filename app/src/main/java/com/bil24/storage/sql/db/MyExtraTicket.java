package com.bil24.storage.sql.db;

import android.support.annotation.NonNull;
import server.net.obj.extra.ExtraTicket;

import java.math.BigDecimal;

/**
 * Created by SVV on 21.09.2015
 */
public class MyExtraTicket extends ExtraTicket {

  private boolean show = true;
  private long actionEventId;

  public MyExtraTicket(long actionEventId, String actionName, long ticketId,
                       String date, String venueName, String venueAddress,
                       String sector, String row, String number,
                       String categoryName, BigDecimal price, String qrCodeImg,
                       String barCodeImg, boolean show, String barCodeNumber) {
    super(ticketId, date, venueName, venueAddress,
        sector, row, number, categoryName, price,
        BigDecimal.ZERO, BigDecimal.ZERO, qrCodeImg, barCodeImg,
        barCodeNumber, actionName, "", "", "");
    this.show = show;
    this.actionEventId = actionEventId;
  }

  public boolean isShow() {
    return show;
  }

  public void setShow(boolean show) {
    this.show = show;
  }

  public long getActionEventId() {
    return actionEventId;
  }

  @NonNull
  public String getSeatInfo() {
    if (getSector() != null) return getSector() + ", " + getRow() + ", " + getNumber();
    else return getCategoryName();
  }
}