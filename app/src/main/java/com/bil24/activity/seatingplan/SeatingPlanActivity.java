package com.bil24.activity.seatingplan;

import android.annotation.SuppressLint;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.webkit.*;
import android.widget.RelativeLayout;
import com.bil24.*;
import com.bil24.dialog.*;
import com.bil24.myelement.MyLoadingPanel;
import com.bil24.storage.*;
import com.bil24.utils.Utils;
import server.net.*;
import server.net.listener.ReservationListener;
import server.net.obj.*;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

public class SeatingPlanActivity extends AppCompatActivity implements ReservationListener, WebViewClient.FinishLoadingListener {
  private static final String EXTRA_PLACEMENT_URL = "EXTRA_PLACEMENT_URL";
  private static final String EXTRA_ACTION_ID = "EXTRA_ACTION_ID";
  private static final String EXTRA_ACTION_NAME = "EXTRA_ACTION_NAME";
  private static final String EXTRA_ACTION_EVENT_TIME = "EXTRA_ACTION_EVENT_TIME";
  private static final String EXTRA_ACTION_EVENT_ID = "EXTRA_ACTION_EVENT_ID";

  private static final DecimalFormat format = new DecimalFormat();
  public static final int RESULT_OPEN_BASKET = 1;

  private WebView webView;
  private String placementUrl;
  private AtomicInteger countTicket = new AtomicInteger(0);
  private RelativeLayout webViewPanel;
  private MyLoadingPanel loadingPanel;
  private com.rey.material.widget.Button buttonReserve;
  private com.rey.material.widget.Button buttonClear;
  private com.rey.material.widget.TextView tvLoadingInfo;
  private Long actionEventId;
  private long actionId;

  private long timeLoadScheme = 0;
  private long timeStartShowScheme = 0;
  private long zipDataSize = 0;

  public static Intent getStartIntent(Context packageContext, String placementUrl, long actionId, String actionName, Long actionEventId, String actionEventTime) {
    Intent intent = new Intent(packageContext, SeatingPlanActivity.class);
    intent.putExtra(EXTRA_PLACEMENT_URL, placementUrl);
    intent.putExtra(EXTRA_ACTION_ID, actionId);
    intent.putExtra(EXTRA_ACTION_NAME, actionName);
    intent.putExtra(EXTRA_ACTION_EVENT_TIME, actionEventTime);
    intent.putExtra(EXTRA_ACTION_EVENT_ID, actionEventId);
    return intent;
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_seating_plan);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

    String actionName = getIntent().getStringExtra(EXTRA_ACTION_NAME);
    String actionEventTime = getIntent().getStringExtra(EXTRA_ACTION_EVENT_TIME);
    setTitle(actionEventTime);

    placementUrl = getIntent().getStringExtra(EXTRA_PLACEMENT_URL);
    actionEventId = getIntent().getLongExtra(EXTRA_ACTION_EVENT_ID, 0);
    actionId = getIntent().getLongExtra(EXTRA_ACTION_ID, 0);

    loadingPanel = (MyLoadingPanel) findViewById(R.id.seatingPlanActivityLoadingPanel);
    loadingPanel.setText("Загрузка схемы...");

    webViewPanel = (RelativeLayout) findViewById(R.id.seatingPlanActivityWebViewPanel);

    com.rey.material.widget.TextView tvActionName = (com.rey.material.widget.TextView) findViewById(R.id.seatingPlanActivityActionName);
    tvActionName.setText(actionName);

    tvLoadingInfo = (com.rey.material.widget.TextView) findViewById(R.id.seatingPlanActivityLoadingInfo);
    tvLoadingInfo.setVisibility(View.GONE);

    buttonReserve = (com.rey.material.widget.Button) findViewById(R.id.seatingPlanButton);
    buttonClear = (com.rey.material.widget.Button) findViewById(R.id.seatingPlanButtonClear);

    buttonReserve.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        close();
      }
    });

    buttonClear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Dialogs.showConfirmDialog(SeatingPlanActivity.this.getSupportFragmentManager(),
            "Сбросить все зарезервированные места на этот сеанс?", new ConfirmListener() {
              @Override
              public void positiveConfirm() {
                NetManager.unReserveAll(SeatingPlanActivity.this, actionEventId);
                start();
              }

              @Override
              public void negativeConfirm() {
              }
            });
      }
    });

    webView = (WebView) findViewById(R.id.webView);
    webView.setWebViewClient(new WebViewClient(webViewPanel, loadingPanel, this));
    webView.setWebChromeClient(new WebChromeClient());
    webView.setBackgroundColor(Color.WHITE);
    WebSettings webSettings = webView.getSettings();
    webView.setInitialScale(1);
    webSettings.setSupportZoom(true);
    webSettings.setBuiltInZoomControls(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setUseWideViewPort(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDefaultTextEncodingName("utf-8");
    webView.addJavascriptInterface(new WebAppInterface(actionId, countTicket, this, buttonReserve, buttonClear, webView), "Android");
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

    start();
    new DownloadSeatingPlan().execute();
  }

  private void start() {
    loadingPanel.setVisibility(View.VISIBLE);
    webViewPanel.setVisibility(View.GONE);
    buttonReserve.setEnabled(false);
    buttonClear.setEnabled(false);
  }

  @Override
  public void onReservation(ReservationClient clientData) {
    if (clientData.getType() == ReservationServer.Type.UN_RESERVE_ALL) {
      Settings.addSeatInReserve(0 - countTicket.get());
      countTicket.set(0);
    }
    new DownloadSeatingPlan().execute();
  }

  @Override
  public void onReservationFailed(NetException e) {
    new DownloadSeatingPlan().execute();
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onLoadFinish() {
    long timeShowScheme = System.currentTimeMillis() - timeStartShowScheme;
    if (SettingsCommon.isVisibleServiceInfo()) {
      tvLoadingInfo.setVisibility(View.VISIBLE);
      tvLoadingInfo.setText("Загрузка: " + format.format(timeLoadScheme) + " ms, " +
          "Отображение: " + format.format(timeShowScheme) + " ms, " +
          "Gzip size: " + format.format(zipDataSize) + " b."
      );
    }
  }

  class DownloadSeatingPlan extends AsyncTask<URL, Integer, String> {

    protected String doInBackground(URL... urls) {
      try {
        long timeTmp = System.currentTimeMillis();
        String svg = loadSeatingPlan();
        timeLoadScheme = System.currentTimeMillis() - timeTmp;
        if (svg == null) return null;
        String html = Utils.readFileFromAssets(SeatingPlanActivity.this, "index.html");
        html = html.replace("#replace", svg);
        return html;
      } catch (Throwable ex) {
        MyUncaughtExceptionHandler.sendError(ex);
        return null;
      }
    }

    protected void onPostExecute(String result) {
      if (result == null) {
        Dialogs.toastShort("Ошибка загрузки схемы зала");
        finish();
        return;
      }
      timeStartShowScheme = System.currentTimeMillis();
      webView.loadDataWithBaseURL("file:///android_asset/", result, "text/html", "UTF-8", null);
    }
  }


  private String loadSeatingPlan() {
    StringBuilder buf = new StringBuilder();
    try {
      URL url = new URL(placementUrl + "&rnd=" + new Random().nextInt(Integer.MAX_VALUE));
      URLConnection conn = url.openConnection();
      conn.setRequestProperty("Accept-Encoding", "gzip");
      zipDataSize = conn.getContentLength();
      InputStreamReader reader = new InputStreamReader(new GZIPInputStream(conn.getInputStream()));
      BufferedReader in = new BufferedReader(reader);

      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        buf.append(inputLine);
      }
      in.close();

      String res = buf.toString();
      if (res.startsWith("{\"")) return null;
      else return res;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private void close() {
    if (countTicket.get() > 0) setResult(RESULT_OPEN_BASKET);
    finish();
  }

  @Override
  public void onBackPressed() {
    finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}