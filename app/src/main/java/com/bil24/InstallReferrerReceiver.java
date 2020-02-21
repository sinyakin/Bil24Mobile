package com.bil24;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bil24.dialog.Dialogs;
import com.bil24.net.AndroidSender;
import com.bil24.storage.FrontendType;
import com.bil24.storage.sql.DataBase;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import server.net.NetException;
import server.net.NetManager;
import server.net.listener.AuthFrontendListener;
import server.net.obj.AuthFrontendClient;

/**
 * User: SVV
 * Date: 19.09.2017.
 */
public class InstallReferrerReceiver extends BroadcastReceiver {
  private static final String TAG = "InstallReferrerReceiver";

  public InstallReferrerReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String referrer = intent.getStringExtra("referrer");
    Log.d(TAG, "referrer=" + referrer);

    try {
      Dialogs.toastShort("получены данные интерфейса");
    } catch (Exception ex) {
      Log.d(TAG, "error while open alert");
    }

    try {
      Intent i = new Intent(Bil24Application.FILTER);
      context.sendBroadcast(i);
    } catch (Exception ex) {
      Log.d(TAG, "error while send broadcast");
    }

    try {
      String result = URLDecoder.decode(referrer, "UTF-8");
      String[] tmp = result.split("&");
      Map<String, String> params = new HashMap<>();
      for (String str : tmp) {
        String[] tmp1 = str.split("=");
        params.put(tmp1[0].toLowerCase(), tmp1[1]);
      }
      long fid = Long.parseLong(params.get("fid"));
      String token = params.get("token");

      if (fid == 0 || token.isEmpty()) {
        Log.d(TAG, "fid or token is empty|" + fid + "|" + token + "|");
        return;
      }

      DataBase db = DataBase.getInstance(context);
      db.mainData.setReferrerAuth(true);
      db.mainData.setDefaultFrontendData(fid, token, FrontendType.ANDROID);

      NetManager.init(new AndroidSender());
      Log.d(TAG, "params=" + params);
    } catch (Exception e) {
      Log.d(TAG, "ERROR=" + e.getMessage());
    }
  }

  public static void authFrontend(final Context context, final long fid, @NonNull final String token, @Nullable final ReferrerAuthListener referrerAuthListener) {
    Log.d(TAG, "auth frontend ..." + fid + " " + token);
    NetManager.authFrontend(new AuthFrontendListener() {
      @Override
      public void onAuthFrontend(AuthFrontendClient clientData) {
        DataBase db = DataBase.getInstance(context);
        if (clientData.getType() != FrontendType.ANDROID.getId()) {
          db.mainData.setMainFrontendData();
          if (referrerAuthListener != null) referrerAuthListener.referrerFinish();
          Log.d(TAG, "onAuthFrontend=not type android | " + FrontendType.get(clientData.getType()));
        } else {
          db.mainData.setDefaultFrontendData(fid, token, FrontendType.get(clientData.getType()));
          if (referrerAuthListener != null) referrerAuthListener.referrerFinish();
          Log.d(TAG, "onAuthFrontend=" + fid);
        }
        db.mainData.setReferrerAuth(false);
      }

      @Override
      public void onAuthFrontendFailed(NetException e) {
        if (e.isAuthFrontendFail()) {
          DataBase db = DataBase.getInstance(context);
          db.mainData.setMainFrontendData();
          if (referrerAuthListener != null) referrerAuthListener.referrerFinish();
          Log.d(TAG, "onAuthFrontendFailed=auth fail");
          db.mainData.setReferrerAuth(false);
        } else {
          authFrontend(context, fid, token, referrerAuthListener);
          Log.d(TAG, "onAuthFrontendFailed=auth frontend...");
        }
      }
    }, fid, token);
  }

  public interface ReferrerAuthListener {
    void referrerFinish();
  }
}
