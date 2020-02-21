package com.bil24.activity.seatingplan;

import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import com.bil24.myelement.MyLoadingPanel;

/**
 * User: SVV
 * Date: 07.07.2015.
 */
class WebViewClient extends android.webkit.WebViewClient {

  private RelativeLayout webViewPanel;
  private MyLoadingPanel loadingPanel;
  private FinishLoadingListener finishLoadingListener;

  WebViewClient(RelativeLayout webViewPanel, MyLoadingPanel myLoadingPanel, FinishLoadingListener finishLoadingListener) {
    this.webViewPanel = webViewPanel;
    this.loadingPanel = myLoadingPanel;
    this.finishLoadingListener = finishLoadingListener;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    view.loadUrl(url);
    return true;
  }

  @Override
  public void onPageFinished(WebView view, String url) {
    if (url.contains("android_asset")) {
      loadingPanel.setVisibility(View.GONE);
      webViewPanel.setVisibility(View.VISIBLE);
    }
    if (finishLoadingListener != null) finishLoadingListener.onLoadFinish();
    super.onPageFinished(view, url);
  }

  interface FinishLoadingListener {
    void onLoadFinish();
  }
}