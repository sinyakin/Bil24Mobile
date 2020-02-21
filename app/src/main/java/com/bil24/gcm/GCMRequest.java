package com.bil24.gcm;

import android.content.Context;
import android.support.annotation.*;

/**
 * Created by SVV on 11.03.2016
 */
public class GCMRequest {
  @NonNull
  private Context context;
  @NonNull
  private GCMCommand command;
  @Nullable
  private GCMResponseListener listener;
  private long fid;

  public GCMRequest(@NonNull Context context, long fid, @NonNull GCMCommand command, @Nullable GCMResponseListener listener) {
    this.context = context;
    this.fid = fid;
    this.command = command;
    this.listener = listener;
  }

  public GCMRequest(@NonNull Context context, long fid, @NonNull GCMCommand command) {
    this.context = context;
    this.fid = fid;
    this.command = command;
  }

  @NonNull
  public Context getContext() {
    return context;
  }

  public long getFid() {
    return fid;
  }

  @NonNull
  public GCMCommand getCommand() {
    return command;
  }

  @Nullable
  public GCMResponseListener getListener() {
    return listener;
  }
}
