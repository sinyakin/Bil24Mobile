package com.bil24.storage;

import android.support.annotation.NonNull;

/**
 * Виктор
 * 11.08.2015.
 */
public class Session {
  private long userId;
  @NonNull
  private String sessionId;
  @NonNull
  private String email;

  public Session(long userId, @NonNull String sessionId, @NonNull String email) {
    this.userId = userId;
    this.sessionId = sessionId;
    this.email = email;
  }

  public boolean isAuth() {
    return userId != 0;
  }

  @NonNull
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
    return "Session{" +
        "userId=" + userId +
        ", sessionId='" + sessionId + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}