package com.bil24.fragments;

import com.bil24.ConnectionManager;
import server.net.NetException;
import server.net.NetManager;
import server.net.listener.GetNewsListener;
import server.net.obj.GetNewsClient;
import server.net.obj.GetNewsServer;

/**
 * Created by SVV on 14.03.2016
 */
class ConnectionManagerNews extends ConnectionManager implements GetNewsListener {

  private static final int timeout = 30000;
  private GetNewsListener listener;
  private int count = 10;
  private long timeNew;
  private boolean pause = false;
  private boolean requestFinish = true;

  ConnectionManagerNews(GetNewsListener listener, int count, long timeNew) {
    this.listener = listener;
    this.count = count;
    this.timeNew = timeNew;
  }

  void setPause(boolean pause) {
    this.pause = pause;
    if (!pause) wakeup();
  }

  void setTimeNew(long timeNew) {
    this.timeNew = timeNew;
  }

  @Override
  public void run() {
    while (!terminated) {
      try {
        pause(timeout);
        if (terminated) break;
        if (pause || !requestFinish) continue;
        requestFinish = false;
        NetManager.getNews(this, timeNew, count, GetNewsServer.CursorMode.forward);
      } catch (Exception ignored) {
      }
    }
  }

  @Override
  public void onGetNews(GetNewsClient clientData) {
    listener.onGetNews(clientData);
    requestFinish = true;
  }

  @Override
  public void onGetNewsFailed(NetException e) {
    listener.onGetNewsFailed(e);
    requestFinish = true;
  }
}
