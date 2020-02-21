package com.bil24.storage.sql.db;

import server.net.obj.extra.ExtraActionEventsGroupedByTickets;

import java.math.BigDecimal;

/**
 * Created by SVV on 06.11.2015
 */
public class MyActionEvent extends ExtraActionEventsGroupedByTickets {
  private long date;
  private boolean delete;

  public MyActionEvent(long actionEventId, String actionName, String fullActionName,
                       String day, String time, String bigPosterUrl, String smallPosterUrl,
                       String cityName, String venueName, int quantity, BigDecimal sum,
                       long date, int delete) {
    super(actionEventId, actionName, fullActionName, day, time, bigPosterUrl, smallPosterUrl, cityName, venueName, quantity, sum, "");
    this.date = date;
    this.delete = (delete == 1);
  }

  public long getDate() {
    return date;
  }

  public boolean isDelete() {
    return delete;
  }
}
