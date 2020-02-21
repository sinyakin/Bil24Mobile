package com.bil24.net;

import server.net.*;
import server.net.obj.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by SVV on 24.11.2015
 */
public class AndroidSenderSynch implements server.net.NetSenderSynch {
  private static SSLSocketFactory sslSocketFactory = null;
  private String address;
  private int port;

  public AndroidSenderSynch(String address, int port) {
    this.address = address;
    this.port = port;
  }

  @SuppressWarnings("TryFinallyCanBeTryWithResources")
  private void init() throws Exception {
    if (sslSocketFactory == null) {
      sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }
  }

  public BaseClientData sendRequest(BaseServerData data) throws NetException {
    try {
      data.setVersionCode(String.valueOf(NetManagerSynch.getVersionCode()));
      data.setIdentificationData(NetManagerSynch.getFid(), NetManagerSynch.getToken());
      if (data instanceof SessionServerData) {
        ((SessionServerData) data).setUserId(NetManagerSynch.getUserId());
        ((SessionServerData) data).setSessionId(NetManagerSynch.getSessionId());
      }

      SSLSocket socket = null;
      BaseClientData readData;
      try {
        init();
        socket = (SSLSocket) sslSocketFactory.createSocket();
        socket.connect(new InetSocketAddress(address, port), 20000);
        socket.setSoTimeout(20000);

        readData = SendingData.send(socket, data);

      } finally {
        if (socket != null) try {
          socket.close();
        } catch (IOException ignored) {
        }
      }
      if (readData.isResult()) return readData;
      else throw new NetException(readData.getDescription(), readData.getResultCode());
    } catch (Exception ex) {
      throw new NetException(ex);
    }
  }
}