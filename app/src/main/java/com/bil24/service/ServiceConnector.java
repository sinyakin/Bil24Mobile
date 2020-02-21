package com.bil24.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import com.bil24.Controller;

public class ServiceConnector implements ServiceConnection, ServiceConnectorListener {
  private Messenger mService;

  /**
   * true-соединен с сервисом
   */
  private boolean mBound;
  private Activity activity;

  public ServiceConnector(Activity activity) {
    this.activity = activity;
    Controller.getInstance().setServiceConnectorListener(this);
  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    mService = new Messenger(service);
    mBound = true;
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    mService = null;
    mBound = false;
  }

  /**
   * Изменить на сервисе вермя паузы между запросами на сервер
   */
  @Override
  public void sendToServiceWakeup() {
    sendToService(MyService.WAKEUP);
  }

  private void sendToService(int command) {
    if (!mBound) return;
    try {
      Message message = Message.obtain(null, command);
      mService.send(message);
    } catch (Exception ignored) {
    }
  }

  /**
   * Соединение с сервисом
   */
  public void connect() {
    activity.bindService(new Intent(activity, MyService.class), this, Context.BIND_AUTO_CREATE);
  }

  /**
   * Отсоединимся от сервиса
   */
  public void disConnect() {
    try {
      if (mBound) {
        activity.unbindService(this);
        mBound = false;
      }
    } catch (Exception ignored) {
    }
  }
}
