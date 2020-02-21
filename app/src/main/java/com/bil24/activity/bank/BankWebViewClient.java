package com.bil24.activity.bank;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.*;

/**
 * User: SVV
 * Date: 07.07.2015.
 */
public class BankWebViewClient extends WebViewClient {
  public static final String successUrl = "https://bankbil24/success";
  public static final String failUrl = "https://bankbil24/error";
  private static final String successUrlTmp = "success";
  private static final String failUrlTmp = "error";
  private static final String urlFirstPage = "payment";
  private static final String urlErrors = "errors_ru";
  private WebView alfaBankWebView;

  private BankPageListener bankPageListener;

  public BankWebViewClient(BankPageListener bankPageListener, WebView alfaBankWebView) {
    this.bankPageListener = bankPageListener;
    this.alfaBankWebView = alfaBankWebView;
  }

 /* @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    Log.d("!!!!!", url);
    if (url.contains(successUrl) || url.contains(successUrlTmp)) {
      if (bankPageListener != null) bankPageListener.result(BankResultPayment.SUCCESS);
      return true;
    } else if (url.contains(failUrl) || url.contains(failUrlTmp)) {
      if (bankPageListener != null) bankPageListener.result(BankResultPayment.ERROR);
      return true;
    }

    view.loadUrl(url);
    return true;
  }*/

  @Override
  public void onPageStarted(WebView view, String url, Bitmap favicon) {
//    Log.d("!!!!!", "onPageStarted " + url);
    if (url.contains(successUrl) || url.contains(successUrlTmp)) {
      if (bankPageListener != null) bankPageListener.result(BankResultPayment.SUCCESS);
      return;
    } else if (url.contains(failUrl) || url.contains(failUrlTmp)) {
      if (bankPageListener != null) bankPageListener.result(BankResultPayment.ERROR);
      return;
    }
    super.onPageStarted(view, url, favicon);
  }

  /*@Override
  public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    super.onReceivedError(view, errorCode, description, failingUrl);
    Log.d("!!!!!", errorCode + " " + description + " " + failingUrl);
  }
*/
  @Override
  public void onPageFinished(WebView view, String url) {
    super.onPageFinished(view, url);
    if (url.contains(urlErrors)) alfaBankWebView.clearHistory();
    if (url.contains(urlFirstPage) && bankPageListener != null) bankPageListener.loadFirstPage();
  }
}