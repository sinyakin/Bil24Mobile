package com.bil24.utils;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.graphics.*;
import android.os.*;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.bil24.Controller;
import com.bil24.storage.SettingsCommon;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.*;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.*;
import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class Utils {
  public static final String TAG = "SVV|";
  public static final String address = "api.bil24.pro";
  public static int port = 8080;
  public static final String NOTIFICATION_CHANNEL_ID = address;

  @SuppressLint("SimpleDateFormat")
  private static SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");
  public static final DecimalFormat sumFormat = new DecimalFormat("#,###,###,##0.00");
  public static final DecimalFormat sumFormatNotKop = new DecimalFormat("#,###,###,##0");

  public static final long DEFAULT_OBJECT_ID = -1;

  static {
    port = (SettingsCommon.isRealZone()) ? 8080 : 1241;
//    port = (SettingsCommon.isRealZone()) ? 1228 : 1241; //для третьей тестовой зоны
  }

  public static Date dateFormat(String date, String dayPattern) {
    try {
      return new SimpleDateFormat(dayPattern, Locale.getDefault()).parse(date);
    } catch (ParseException e) {
      return new Date();
    }
  }

  public static String formatedRub(BigDecimal sum) {
    return sumFormat.format(sum) + " руб.";
  }

  public static String formatedRubNotKop(BigDecimal sum) {
    return sumFormatNotKop.format(sum) + " руб.";
  }

  public static String dateFormat(Date date, String dayPattern) {
    return new SimpleDateFormat(dayPattern, Locale.getDefault()).format(date);
  }

  public static synchronized String dateFormatDDMMYYYY(Date date) {
    return dayFormat.format(date);
  }

  public static synchronized Date dateFormatDDMMYYYY(String date) {
    try {
      return dayFormat.parse(date);
    } catch (ParseException e) {
      return new Date();
    }
  }

  public static void hideSoftKeyboard(Activity activity) {
    try {
      InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
      if (activity.getCurrentFocus() != null)
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    } catch (Exception ignored) {
    }
  }

  //100% работает
  public static void hideKeyboard(Activity activity) {
    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  public static ApplicationInfo getAppInfo(Context context) {
    return new ApplicationInfo(getDeviceName() + " OS " + getAndroidVersion(), String.valueOf(getVersionCode(context)), getVersionName(context));
  }

  @NonNull
  public static Integer getVersionCode(Context context) {
    try {
      String pkg = context.getPackageName();
      return context.getPackageManager().getPackageInfo(pkg, 0).versionCode;
    } catch (Exception e) {
      return 0;
    }
  }

  @NonNull
  private static String getVersionName(Context context) {
    try {
      String pkg = context.getPackageName();
      return context.getPackageManager().getPackageInfo(pkg, 0).versionName;
    } catch (Exception e) {
      return "";
    }
  }

  @NonNull
  private static String getDeviceName() {
    try {
      String manufacturer = Build.MANUFACTURER;
      String model = Build.MODEL;
      if (model.startsWith(manufacturer)) return capitalize(model);
      else return capitalize(manufacturer) + " " + model;
    } catch (Exception ignored) {}
    return "";
  }

  private static String capitalize(String s) {
    if (s == null || s.length() == 0) return "";
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) return s;
    else return Character.toUpperCase(first) + s.substring(1);
  }

  private static String getAndroidVersion() {
    return android.os.Build.VERSION.RELEASE;
  }

  public static String createHash(String md5) {
    String result = "";
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(md5.getBytes("UTF-8"));
      byte[] bytes = md.digest();
      StringBuilder sb = new StringBuilder(bytes.length * 2);
      Formatter formatter = new Formatter(sb);
      for (byte b : bytes) formatter.format("%02x", b);
      result = sb.toString();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public static Bitmap getBitmap(String img) {
    byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
  }

  public static String getBase64Img(byte[] img) {
    return Base64.encodeToString(img, Base64.DEFAULT);
  }

  public static float dpFromPx(final Context context, final float px) {
    return px / context.getResources().getDisplayMetrics().density;
  }

  public static float pxFromDp(final Context context, final float dp) {
    return dp * context.getResources().getDisplayMetrics().density;
  }

  public static String readFileFromAssets(Activity activity, String fileName) {
    try {
      StringBuilder buf = new StringBuilder();
      InputStream inputStream = activity.getAssets().open(fileName);
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      String str;

      while ((str = in.readLine()) != null) {
        buf.append(str);
      }

      in.close();
      return buf.toString();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "";
  }

  public static byte[] readByteFromAssets(Activity activity, String fileName) {
    try {
      InputStream inputStream = activity.getAssets().open(fileName);
      return readFully(inputStream);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new byte[0];
  }

  public static byte[] readFully(InputStream input) throws IOException {
    byte[] buffer = new byte[8192];
    int bytesRead;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    while ((bytesRead = input.read(buffer)) != -1) {
      output.write(buffer, 0, bytesRead);
    }
    return output.toByteArray();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void clearDirectory(File dir) {
    if (dir.isDirectory()) {
      File[] files = dir.listFiles();
      if (files != null && files.length > 0) {
        for (File aFile : files) {
          aFile.delete();
        }
      }
    }
  }

  /* Checks if external storage is available for read and write */
  public static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state);
  }

  /* Checks if external storage is available to at least read */
  public static boolean isExternalStorageReadable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state) ||
        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
  }

  public static boolean isValidEmail(CharSequence target) {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
  }

  public static boolean isMyServiceRunning(Activity activity, Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
    for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if(serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check the device to make sure it has the Google Play Services APK. If
   * it doesn't, display a dialog that allows users to download the APK from
   * the Google Play Store or enable it in the device's system settings.
   */
  public static boolean checkPlayServices(Context context, Activity activity) {
    try {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
      if (resultCode != ConnectionResult.SUCCESS) {
        if (apiAvailability.isUserResolvableError(resultCode) && !Controller.getInstance().isKassatka()) {
          apiAvailability.getErrorDialog(activity, resultCode, 900).show();
        }
        return false;
      }
    } catch (Exception ex) {
      return false;
    }
    return true;
  }
}