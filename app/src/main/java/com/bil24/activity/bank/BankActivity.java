package com.bil24.activity.bank;

import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.*;
import android.view.*;
import android.webkit.*;
import com.bil24.R;
import com.bil24.myelement.MyLoadingPanel;
import com.bil24.storage.Settings;

public class BankActivity extends AppCompatActivity implements BankPageListener {
  private static final String EXTRA_URL = "EXTRA_URL";
  public static final String EXTRA_ORDER_ID = "EXTRA_ORDER_ID";
  public static final String EXTRA_ONLY_MEC = "EXTRA_ONLY_MEC";

  public static final int RESULT_OK = 1;
  public static final int RESULT_ERROR = 2;
  public static final int RESULT_CANCELED = 3;
  public static final int RESULT_OK_MEC = 4;

  private MyLoadingPanel bankActivityLoadPagePanel;
  private WebView webView;

  public static Intent getStartIntent(Context context, Long orderId, String formUrl, boolean openMECFragmentAfterPaid) {
    Intent intent = new Intent(context, BankActivity.class);
    intent.putExtra(EXTRA_ORDER_ID, orderId);
    intent.putExtra(EXTRA_URL, formUrl);
    intent.putExtra(EXTRA_ONLY_MEC, openMECFragmentAfterPaid);
    return intent;
  }
  private long orderId;
  private boolean openMECFragmentAfterPaid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_alfa_bank);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

    String formUrl = getIntent().getStringExtra(EXTRA_URL);
    orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, 0);
    openMECFragmentAfterPaid = getIntent().getBooleanExtra(EXTRA_ONLY_MEC, false);

    bankActivityLoadPagePanel = (MyLoadingPanel) findViewById(R.id.alfaBankActivityLoadPagePanel);

    webView = (WebView) findViewById(R.id.alfaBankActivityWebView);
    webView.setVisibility(View.GONE);
    webView.setWebViewClient(new BankWebViewClient(this, webView));
    webView.setWebChromeClient(new WebChromeClient());
    webView.setBackgroundColor(Color.WHITE);
    webView.setInitialScale(1);
    WebSettings webSettings = webView.getSettings();
    webSettings.setSupportZoom(true);
    webSettings.setBuiltInZoomControls(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setUseWideViewPort(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    webSettings.setDomStorageEnabled(true);

    bankActivityLoadPagePanel.setText("Загрузка платежной страницы");
    webView.loadUrl(formUrl);
    setTitle("Оплата картой");
  }

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else resultCanceled();
  }

  @Override
  public void result(BankResultPayment alfaResultPayment) {
    switch (alfaResultPayment) {
      case SUCCESS: {
        resultOk();
        break;
      }
      case ERROR:{
        resultError();
        break;
      }
    }
  }

  private void resultOk() {
    Settings.setOrderId(orderId);
    if (openMECFragmentAfterPaid) setResult(RESULT_OK_MEC);
    else setResult(RESULT_OK);
    finish();
  }

  private void resultError() {
    setResult(RESULT_ERROR);
    finish();
  }

  private void resultCanceled() {
    Intent intent = new Intent();
    intent.putExtra(EXTRA_ORDER_ID, orderId);
    setResult(RESULT_CANCELED, intent);
    finish();
  }

  @Override
  public void loadFirstPage() {
    bankActivityLoadPagePanel.setVisibility(View.GONE);
    webView.setVisibility(View.VISIBLE);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        resultCanceled();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
