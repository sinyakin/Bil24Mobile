package com.bil24.gcm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by SVV on 11.03.2016
 */
public class GCMResponse {
  @NonNull
  private GCMCommand command;
  @Nullable
  private GCMResponseListener listener;
  @Nullable
  private String token;
  private boolean ok = false;

  public GCMResponse(@NonNull GCMCommand command, @Nullable GCMResponseListener listener) {
    this.command = command;
    this.listener = listener;
  }

  @NonNull
  public GCMCommand getCommand() {
    return command;
  }

  @Nullable
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Nullable
  public GCMResponseListener getListener() {
    return listener;
  }

  public void ok() {
    this.ok = true;
  }

  public void fail() {
    this.ok = false;
  }

  public boolean isOk() {
    return ok;
  }
}
