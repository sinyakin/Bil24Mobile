package com.bil24.storage;

import android.support.annotation.NonNull;

import com.bil24.utils.Utils;

/**
 * Created by SVV on 02.06.2016
 */
public class FrontendData {
  //параметры default инициализируются только при старте приложения
  private long defaultFid;
  @NonNull private String defaultToken;
  @NonNull private FrontendType defaultFrontendType;

  private long newFid;
  @NonNull private String newToken;
  @NonNull private FrontendType newFrontendType;

  public FrontendData(long defaultFid, @NonNull String defaultToken, @NonNull FrontendType defaultFrontendType,
                      long newFid, @NonNull String newToken, @NonNull FrontendType newFrontendType) {
    this.defaultFid = defaultFid;
    this.defaultToken = defaultToken;
    this.defaultFrontendType = defaultFrontendType;
    this.newFid = newFid;
    this.newToken = newToken;
    this.newFrontendType = newFrontendType;
  }

  public static FrontendData getDefault() {
    return new FrontendData(Utils.DEFAULT_OBJECT_ID, "", FrontendType.UNKNOWN, Utils.DEFAULT_OBJECT_ID, "", FrontendType.UNKNOWN);
  }

  public boolean isNewFrontend() {
    return newFid != Utils.DEFAULT_OBJECT_ID;
  }

  public long getFid() {
    return isNewFrontend() ? newFid : defaultFid;
  }

  @NonNull
  public String getToken() {
    return isNewFrontend() ? newToken : defaultToken;
  }

  @NonNull
  public FrontendType getFrontendType() {
    return isNewFrontend() ? newFrontendType : defaultFrontendType;
  }

  public long getDefaultFid() {
    return defaultFid;
  }

  @NonNull
  public String getDefaultToken() {
    return defaultToken;
  }

  @NonNull
  public FrontendType getDefaultFrontendType() {
    return defaultFrontendType;
  }

  @Override
  public String toString() {
    return "FrontendData{" +
        "defaultFid=" + defaultFid +
        ", defaultToken='" + defaultToken + '\'' +
        ", defaultFrontendType=" + defaultFrontendType +
        ", newFid=" + newFid +
        ", newToken='" + newToken + '\'' +
        ", newFrontendType=" + newFrontendType +
        '}';
  }
}
