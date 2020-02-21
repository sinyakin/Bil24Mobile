package com.bil24.gcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.bil24.R;
import com.bil24.activity.MainActivity;
import com.bil24.storage.*;
import com.bil24.storage.SettingsCommon;
import com.bil24.storage.sql.DataBase;
import com.bil24.utils.Utils;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by SVV on 25.02.2016
 */
public class MyGcmListenerService extends GcmListenerService {
  public static final String BROADCAST_FILTER = "bil24.new_push";

  private static final String TAG = "MyGcmListenerService";
  private static final String newsTopic = "/topics/news";

  @Override
  public void onMessageReceived(String from, Bundle data) {
    try {
      if (from.startsWith(newsTopic)) {
        long newsId = Long.parseLong(data.getString("id"));
        String title = data.getString("title");
        String message = data.getString("message");
        String zoneName = data.getString("zone").toLowerCase().trim();

        long fid = DataBase.getInstance(getApplicationContext()).mainData.getFrontendData().getFid();
        Zone zone = Zone.valueOf(zoneName);
        Log.d(TAG, "zone = " + zone + " SettingsCommon.isRealZone = " + SettingsCommon.isRealZone() + " fid = " + fid);
        if ((zone == Zone.real && SettingsCommon.isRealZone() && from.contains(String.valueOf(fid))) ||
            (zone == Zone.test && !SettingsCommon.isRealZone()) && from.contains(String.valueOf(fid))) {
          sendNotification(newsId, title, message);
        }
      } else {
        // normal downstream message.
        /*long newsId = Long.parseLong(data.getString("id"));
        String title = data.getString("title");
        String message = data.getString("message");
        String zoneName = data.getString("zone").toLowerCase().trim();

        long fid = DataBase.getInstance(getApplicationContext()).mainData.getFrontendData().getFid();
        Zone zone = Zone.valueOf(zoneName);
        Log.d(TAG, "zone = " + zone + " SettingsCommon.isRealZone = " + SettingsCommon.isRealZone() + " fid = " + fid);
        sendNotification(newsId, title, message);*/
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Log.d(TAG, ex.getMessage());
    }
  }

  private void sendNotification(long newsId, String title, String message) {
    if (Settings.existNewsId(newsId)) return;
    Settings.setReadNewsId(newsId);

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && !SettingsCommon.isCreatedChannel()) {
      CharSequence name = "Оповещения";
      String description = "Новости bil24";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel mChannel = new NotificationChannel(Utils.NOTIFICATION_CHANNEL_ID, name, importance);
      mChannel.setDescription(description);
      mChannel.enableLights(true);
      mChannel.enableVibration(true);
      mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
      mChannel.setShowBadge(false);
      notificationManager.createNotificationChannel(mChannel);
      SettingsCommon.createChannel();
    }

    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_SHOW_NEWS, true);
    intent.setAction(Intent.ACTION_MAIN);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT);

    Uri defaultSoundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.bil);

    Notification.Builder builder;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      builder = new Notification.Builder(this, Utils.NOTIFICATION_CHANNEL_ID);
    } else {
      builder = new Notification.Builder(this);
    }

    Notification notification = builder
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(message)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)
        .build();

    int notificationId = SettingsCommon.incNotificationId();
    notificationManager.notify(notificationId, notification);
    //отправим оповещение в активность о новом пуше
    Intent registrationComplete = new Intent(BROADCAST_FILTER);
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
  }

  private enum Zone {
    test, real
  }
}