package com.bil24.net;

import server.net.*;
import server.net.obj.BaseClientData;

public class Response {
  private BaseClientData clientData;
  private NetException netException;
  private NetListener netListener;

  public void setClientData(BaseClientData clientData) {
    this.clientData = clientData;
  }

  public void setNetException(NetException netException) {
    this.netException = netException;
  }

  public BaseClientData getClientData() {
    return clientData;
  }

  public NetException getNetException() {
    return netException;
  }

  public NetListener getNetListener() {
    return netListener;
  }

  public void setNetListener(NetListener netListener) {
    this.netListener = netListener;
  }
}
