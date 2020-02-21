package com.bil24.net.utils;

import java.text.DecimalFormat;

/**
 * Created by SVV on 27.02.2016
 */
public class NetTelemetry {
  private static final DecimalFormat format = new DecimalFormat();

  private final String command;

  private volatile long sentBytes = -1L;       //объем отправленных данных
  private volatile long receivedBytes = -1L;   //объем принятых данных
  private volatile long receivedBytesZip = -1L;//объем принятых данных перед разархивацией
  private volatile long processingTime = -1L;

  public NetTelemetry(String command) {
    this.command = command;
  }

  public long getSentBytes() {
    return sentBytes;
  }

  public void setSentBytes(long sentBytes) {
    this.sentBytes = sentBytes;
  }

  public long getReceivedBytes() {
    return receivedBytes;
  }

  public void setReceivedBytes(long receivedBytes) {
    this.receivedBytes = receivedBytes;
  }

  public long getReceivedBytesZip() {
    return receivedBytesZip;
  }

  public void setReceivedBytesZip(long receivedBytesZip) {
    this.receivedBytesZip = receivedBytesZip;
  }

  public long getProcessingTime() {
    return processingTime;
  }

  public void setProcessingTime(long processingTime) {
    this.processingTime = processingTime;
  }

  @Override
  public String toString() {
    String result = "NetTelemetry{Command: " + command + ", sent: " + format.format(sentBytes) + " b";
    result += ", received: " + format.format(receivedBytes) + " b";
    result += " (" + format.format(receivedBytesZip) + " b)";
    result += ", time: " + format.format(processingTime) + " ms}";
    return result;
  }
}
