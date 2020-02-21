package com.bil24.net;

import com.bil24.*;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;
import com.bil24.utils.Utils;
import server.net.*;
import server.net.obj.*;

public class AndroidSender implements NetSender {

  @Override
  public void sendRequest(NetListener listener, BaseServerData data) {
    try {
      if (!data.existFrontendData()) {
        FrontendData frontendData = Controller.getInstance().getFrontendData(Bil24Application.getContext());
        data.setIdentificationData(frontendData.getFid(), frontendData.getToken());
      }
    } catch (Exception ignored) {}

    data.setVersionCode(String.valueOf(Utils.getVersionCode(Bil24Application.getContext())));
    if (data instanceof SessionServerData) {
      Session session = DataBase.getSession(Bil24Application.getContext());
      ((SessionServerData) data).setUserId(session.getUserId());
      ((SessionServerData) data).setSessionId(session.getSessionId());
    }

    new DoRequest().execute(new Request(listener, data));
  }
}
