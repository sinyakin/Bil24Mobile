package com.bil24.storage;

import android.support.annotation.*;

/**
 * Created by SVV on 28.10.2016
 */

public class LastUserData {
  private long userId;
  @NonNull private String sessionId;
  @NonNull private String email;

  public LastUserData(long userId, @NonNull String sessionId, @NonNull String email) {
    this.userId = userId;
    this.sessionId = sessionId;
    this.email = email;
  }

  public Long getUserId() {
    return userId;
  }

  @NonNull
  public String getSessionId() {
    return sessionId;
  }

  @NonNull
  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "LastUserData{" +
        "userId=" + userId +
        ", sessionId='" + sessionId + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
