package com.bil24.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;

import com.bil24.Bil24Application;
import com.bil24.net.AndroidSenderSynch;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;
import com.bil24.storage.sql.db.MyError;
import com.bil24.utils.Utils;
import server.net.NetManagerSynch;

import java.util.List;
import java.util.concurrent.TimeUnit;

//todo запускать сервис только когда нужно
public class MyService extends Service {
  public static final int WAKEUP = 1;

  private boolean waking = false;
  private boolean end = false;

  private Messenger messenger = new Messenger(new Handler() {
    @Override
    public void handleMessage(Message msg) {
      try {
        switch (msg.what) {
          case WAKEUP:
            wakeup();
            break;
        }
      } catch (Exception ignored) {
      }
      super.handleMessage(msg);
    }
  });

  public MyService() {
  }

  public int onStartCommand(Intent intent, int flags, final int startId) {

    try {
      FrontendData frontendData = DataBase.getInstance(Bil24Application.getContext()).mainData.getFrontendData();
      NetManagerSynch.init(
          new AndroidSenderSynch(Utils.address, Utils.port),
          Utils.getVersionCode(getApplicationContext()),
          frontendData.getFid(), frontendData.getToken());
    } catch (Exception ignored) {}

    wakeup();

    new Thread(new Runnable() {
      @Override
      public void run() {

        while (true) {
          try {
            pause(60);
            if (end) break;

            if (NetManagerSynch.getSessionId() == null) {
              Session session = DataBase.getSession(getApplicationContext());
              NetManagerSynch.setSessionData(session.getUserId(), session.getSessionId());
            } else continue;

            DataBase dataBase = DataBase.getInstance(getApplicationContext());
            List<MyError> errorList = dataBase.error.getErrorsNotSend();
            for (MyError myError : errorList) {
              NetManagerSynch.sendError(
                  myError.getVersionName(), myError.getVersionCode(),
                  myError.getDeviceName(), myError.getError(),
                  NetManagerSynch.getUserId(), NetManagerSynch.getSessionId());
              dataBase.error.delErrorById(myError.getId());
            }
          } catch (Exception ignored) {
          }
        }
      }
    }).start();

    return START_REDELIVER_INTENT;
  }

  /**
   * @param delay время сна в сек
   */
  private void pause(int delay) throws Exception {
    int ticks = 0;
    int RESPONSE = 2;
    delay = delay / RESPONSE;
    while ((ticks++ < delay) && !waking) {
      TimeUnit.SECONDS.sleep(RESPONSE);
    }
    waking = false;
  }

  public void wakeup() {
    waking = true;
  }

  public void onDestroy() {
    super.onDestroy();
    end = true;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return messenger.getBinder();
  }
}