package com.bil24.utils;

/**
 * Created by SVV on 08.12.2015
 */
public class ApplicationInfo {
  private String device;
  private String versionCode;
  private String versionName;

  public ApplicationInfo(String device, String versionCode, String versionName) {
    this.device = device;
    this.versionCode = versionCode;
    this.versionName = versionName;
  }

  public String getDevice() {
    return device;
  }

  public String getVersionCode() {
    return versionCode;
  }

  public String getVersionName() {
    return versionName;
  }
}
