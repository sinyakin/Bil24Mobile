package com.bil24.net;

import server.net.NetListener;
import server.net.obj.BaseServerData;

public class Request {
  private NetListener netListener;
  private BaseServerData serverData;

  public Request(NetListener netListener, BaseServerData serverData) {
    this.netListener = netListener;
    this.serverData = serverData;
  }

  public NetListener getNetListener() {
    return netListener;
  }

  public BaseServerData getServerData() {
    return serverData;
  }
}
