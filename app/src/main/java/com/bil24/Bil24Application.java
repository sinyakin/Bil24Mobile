package com.bil24;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.bil24.dialog.Dialogs;
import com.squareup.picasso.*;

/**
 * User: SVV
 * Date: 29.05.2015.
 */
public class Bil24Application extends MultiDexApplication {
  private static Bil24Application instance;
  private BroadcastReceiver broadcastReceiver;
  public final static String FILTER = "android.intent.action.MAIN.SVV";

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    Picasso.Builder b = new Picasso.Builder(this);
    b.downloader(new OkHttpDownloader(this));
    Picasso.setSingletonInstance(b.build());
    Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());

    IntentFilter intentFilter = new IntentFilter(FILTER);
    broadcastReceiver = new BroadcastReceiver() {

      @Override
      public void onReceive(final Context context, final Intent intent) {
        Controller.getInstance().clearFrontendData();
      }
    };
    //registering our receiver
    this.registerReceiver(broadcastReceiver, intentFilter);
  }

  @Override
  public void onTerminate() {
    if (broadcastReceiver != null) this.unregisterReceiver(broadcastReceiver);
    super.onTerminate();
  }

  public static Context getContext() {
    return instance.getApplicationContext();
  }

  public static boolean withMiniDrawer() {
    int screenSize = getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    return screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }
}
