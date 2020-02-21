package com.bil24.gcm;

/**
 * Created by SVV on 11.03.2016
 */
public interface GCMResponseListener {
  void onGCMSuccess(GCMCommand command, String token);
  void onGCMFail(GCMCommand command);
}
