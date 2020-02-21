package com.bil24.net;

import android.os.AsyncTask;
import com.bil24.*;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.sql.DataBase;
import com.bil24.utils.Utils;
import server.en.Command;
import server.net.*;
import server.net.obj.BaseClientData;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;

public class DoRequest extends AsyncTask<Request, Integer, Response> {

  private static SSLSocketFactory sslSocketFactory = null;
  private static boolean error1 = false;
  private static boolean error2 = false;

  @Override
  protected Response doInBackground(Request... requests) {
    return sendRequest(requests[0]);
  }

  @Override
  protected void onPostExecute(Response response) {
    super.onPostExecute(response);
    NetListener netListener = response.getNetListener();

    if (response.getClientData() != null &&
        response.getClientData().getResultCode() == 1 && response.getClientData().getCommand() != Command.AUTH) {
      DataBase.clearSession(Bil24Application.getContext());
      Dialogs.toastShort(Bil24Application.getContext().getString(R.string.err_net_auth));
    }

    if (response.getClientData() != null) {
      if (netListener != null) netListener.onResult(response.getClientData());
      error1 = false;
      error2 = false;
      return;
    }

    if (netListener != null) netListener.onResultFailed(response.getNetException());

    if (response.getNetException().getCause() instanceof ConnectException || response.getNetException().getCause() instanceof SocketTimeoutException) {
      if (!error1) Dialogs.toastShort(Bil24Application.getContext().getString(R.string.err_net_server));
      error1 = true;
    } else if (response.getNetException().getCause() instanceof IOException) {
      if (!error2) Dialogs.toastShort(Bil24Application.getContext().getString(R.string.err_net));
      error2 = true;
    }
  }

  private void init() throws Exception {
    if (sslSocketFactory == null) {
      sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }
  }

  private Response sendRequest(Request request) {
    Response response = new Response();
    response.setNetListener(request.getNetListener());
    try {
      init();
      SSLSocket socket = null;
      BaseClientData readData;
      try {
        socket = (SSLSocket) sslSocketFactory.createSocket();
        socket.connect(new InetSocketAddress(Utils.address, Utils.port), 15000);
        socket.setSoTimeout(15000);

        readData = SendingData.send(socket, request.getServerData());
      } finally {
        if (socket != null) try {
          socket.close();
        } catch (IOException ignored) {
        }
      }
      if (readData.isResult()) {
        response.setClientData(readData);
      } else {
        response.setNetException(new NetException(readData.getDescription(), readData.getResultCode()));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      response.setNetException(new NetException(ex));
    }
    return response;
  }
}