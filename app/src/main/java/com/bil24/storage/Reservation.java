package com.bil24.storage;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.bil24.utils.*;

/**
 * Зарезервированные места по session id
 * User: SVV
 * Date: 16.06.2015.
 */
@Deprecated
public class Reservation implements Serializable {
  static final long serialVersionUID = 4545645645678946543L;

  private long actionId;
  private String sessionId;
  private long orderId;
  /**
   * key - calendarId
   * value - set seatId
   */
  private Map<Long, Set<Long>> reserveMap = new ConcurrentHashMap<>();

  public Reservation(long actionId) {
    this.actionId = actionId;
    this.sessionId = Utils.createHash(new Random().nextInt(Integer.MAX_VALUE) + "_" + System.currentTimeMillis());
  }

  public String getSessionId() {
    return sessionId;
  }

  public synchronized void setSessionId(String sessionId) {
    this.sessionId = sessionId;
//    Settings.updateReservation(this);
  }

  public long getOrderId() {
    return orderId;
  }

  public synchronized void setOrderId(long orderId) {
    this.orderId = orderId;
//    Settings.updateReservation(this);
  }

  public long getActionId() {
    return actionId;
  }

  public Map<Long, Set<Long>> getReserveMap() {
    return new HashMap<>(reserveMap);
  }

  /**
   * @return кол- во зарезервированных мест
   */
  public int getCountReserve() {
    int count = 0;
    for (Set<Long> longs : getReserveMap().values()) {
      count += longs.size();
    }
    return count;
  }

  public synchronized void addReserve(Long calendarId, Long seatId) {
    Set<Long> seatMap = reserveMap.get(calendarId);
    if (seatMap == null) {
      seatMap = new TreeSet<>();
      reserveMap.put(calendarId, seatMap);
    }
    seatMap.add(seatId);
//    Settings.updateReservation(this);
  }

  public synchronized void removeReserve(Long calendarId, Long seatId) {
    Set<Long> seatMap = reserveMap.get(calendarId);
    if (seatMap == null) return;
    seatMap.remove(seatId);
//    Settings.updateReservation(this);
  }
}
