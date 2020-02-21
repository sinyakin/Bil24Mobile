package com.bil24.storage.sql.db;

/**
 * Created by SVV on 03.12.2015
 */
public class MyError {
  private Integer id;
  private String deviceName;
  private String versionName;
  private String versionCode;
  private String error;

  public MyError(Integer id, String deviceName, String versionName, String versionCode, String error) {
    this.id = id;
    this.deviceName = deviceName;
    this.versionName = versionName;
    this.versionCode = versionCode;
    this.error = error;
  }

  public Integer getId() {
    return id;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public String getVersionName() {
    return versionName;
  }

  public String getVersionCode() {
    return versionCode;
  }

  public String getError() {
    return error;
  }

  @Override
  public String toString() {
    return "MyError{" +
        "id=" + id +
        ", deviceName='" + deviceName + '\'' +
        ", versionName='" + versionName + '\'' +
        ", versionCode='" + versionCode + '\'' +
        ", error='" + error + '\'' +
        '}';
  }
}
