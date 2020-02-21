package com.bil24.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.bil24.storage.*;
import com.google.android.gms.gcm.*;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by SVV on 11.03.2016
 */
public class GCMDoRequest extends AsyncTask<GCMRequest, Integer, GCMResponse> {
  private static final String TAG = "GCMDoRequest";
  private static final String gcmId = "141002926501";

  @Override
  protected GCMResponse doInBackground(GCMRequest... params) {
    GCMRequest request = params[0];
    GCMResponse response = new GCMResponse(request.getCommand(), request.getListener());
    try {
      GCMCommand command = request.getCommand();
      InstanceID instanceID = InstanceID.getInstance(request.getContext());
      String token = instanceID.getToken(gcmId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      if (token == null || token.isEmpty()) return response;
      String memoryPushToken = Settings.getPushToken();
      if (memoryPushToken != null && !memoryPushToken.equals(token)) {
        //если токен изменился сделаем пометку что он не отправлялся
        Settings.sentTokenToServer(false);
      }
      response.setToken(token);
      Settings.setPushToken(token);
      Log.d(TAG, command + " GCM Registration Token: " + token);

      switch (command) {
        case GET_TOKEN:
          Boolean notNews = SettingsCommon.isNotificationNews(request.getFid());
          if (notNews == null || !notNews) {
            subscribeTopics(request.getContext(), token, request.getFid());
          }
          break;
        case SUBSCRIBE:
          subscribeTopics(request.getContext(), token, request.getFid());
          break;
        case UN_SUBSCRIBE:
          unSubscribeTopics(request.getContext(), token, request.getFid());
          break;
      }

      response.ok();
    } catch (Exception e) {
      Log.d(TAG, "Failed to complete token refresh", e);
      response.fail();
    }
    return response;
  }

  private void subscribeTopics(Context context, String token, long fid) throws IOException {
    GcmPubSub pubSub = GcmPubSub.getInstance(context);
    pubSub.subscribe(token, "/topics/news", null);
    pubSub.subscribe(token, "/topics/news_" + fid, null);
    SettingsCommon.setNotificationNews(true, fid);
  }

  private void unSubscribeTopics(Context context, String token, long fid) throws IOException {
    GcmPubSub pubSub = GcmPubSub.getInstance(context);
    pubSub.unsubscribe(token, "/topics/news");
    pubSub.unsubscribe(token, "/topics/news_" + fid);
    SettingsCommon.setNotificationNews(false, fid);
  }

  @Override
  protected void onPostExecute(GCMResponse response) {
    super.onPostExecute(response);
    if (response.getListener() == null) return;
    if (response.isOk()) {
      response.getListener().onGCMSuccess(response.getCommand(), response.getToken());
    } else {
      response.getListener().onGCMFail(response.getCommand());
    }
  }
}
