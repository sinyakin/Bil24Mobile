package com.bil24.net;

import android.util.Log;
import com.bil24.net.utils.*;
import server.net.obj.*;

import java.io.*;
import java.net.Socket;
import java.util.zip.GZIPInputStream;

/**
 * Created by SVV on 27.02.2016
 */
public class SendingData {
  public static boolean debug = true;

  public static BaseClientData send(Socket socket, BaseServerData data) throws Exception {
    NetTelemetry telemetry = new NetTelemetry(data.getCommand().name());
    long time = System.currentTimeMillis();

    CountingOutputStream countingOutputStream = new CountingOutputStream(new BufferedOutputStream(socket.getOutputStream()), 512);
    ObjectOutputStream objout = new ObjectOutputStream(countingOutputStream);
    objout.writeObject(data);
    objout.flush();

    long inDeltaZip;//сэкономлено байт за счет сжатия
    CountingInputStream inc = new CountingInputStream(new BufferedInputStream(socket.getInputStream()), 1024);
    long bytes = inc.getByteCount();
    CountingInputStream zipCountingInputStream = new CountingInputStream(new GZIPInputStream(inc), bytes);
    ObjectInputStream in = new ObjectInputStream(zipCountingInputStream);
    BaseClientData readData = (BaseClientData) in.readObject();

    inDeltaZip = zipCountingInputStream.getByteCount() - inc.getByteCount() - 1;//"GZIP".length()-"OBJ".length() == 1
    telemetry.setProcessingTime(System.currentTimeMillis() - time);
    telemetry.setSentBytes(countingOutputStream.getByteCount());
    telemetry.setReceivedBytes(inc.getByteCount() + inDeltaZip);
    telemetry.setReceivedBytesZip(inc.getByteCount());

    if (debug) {
      Log.d("NetTelemetry|", telemetry.toString());
    }

    return readData;
  }

}
