package com.bil24.fragments.wifly;

import android.util.Log;
import android.webkit.*;

/**
 * User: SVV
 * Date: 07.07.2015.
 */
public class WyFlyWebViewClient extends WebViewClient {
  private static final String urlFirstPage = "enter.wi-fly.net";
  private WebView webView;

  private WifyPageListener wifyPageListener;

  public WyFlyWebViewClient(WifyPageListener wifyPageListener, WebView webView) {
    this.wifyPageListener = wifyPageListener;
    this.webView = webView;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    Log.d("!!!!!", url);
    view.loadUrl(url);
    return true;
  }

  @Override
  public void onPageFinished(WebView view, String url) {
    super.onPageFinished(view, url);
    if (url.contains(urlFirstPage) && wifyPageListener != null) wifyPageListener.loadFirstPage();
  }
}