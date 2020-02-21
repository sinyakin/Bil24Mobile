package com.bil24;

import android.content.Context;
import com.bil24.storage.sql.DataBase;
import com.bil24.utils.ApplicationInfo;
import com.bil24.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by SVV on 01.12.2015
 */
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  private Thread.UncaughtExceptionHandler oldHandler;
  private static ApplicationInfo applicationInfo = Utils.getAppInfo(Bil24Application.getContext());

  public MyUncaughtExceptionHandler() {
    oldHandler = Thread.getDefaultUncaughtExceptionHandler();
  }

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    sendError(throwable);
    if (oldHandler != null) oldHandler.uncaughtException(thread, throwable);
  }

  public static void sendError(Throwable throwable) {
    DataBase.getInstance(Bil24Application.getContext()).error.addError(applicationInfo, getStackTraceString(throwable));
  }

  private static String getStackTraceString(Throwable tr) {
    StringWriter result = new StringWriter();
    PrintWriter printWriter = new PrintWriter(result);

    Throwable cause = tr;
    while (cause != null) {
      cause.printStackTrace(printWriter);
      cause = cause.getCause();
    }
    String stacktraceAsString = result.toString();
    printWriter.close();

    return stacktraceAsString;
  }
}
