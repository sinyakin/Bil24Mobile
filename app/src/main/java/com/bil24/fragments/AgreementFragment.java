package com.bil24.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.bil24.R;
import com.bil24.myelement.MyLoadingPanel;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class AgreementFragment extends Fragment {

  @BindView(R.id.agreementFragmentLoadingPanel)
  MyLoadingPanel loadingPanel;

  @BindView(R.id.agreementFragmentWebView)
  WebView webView;
  private Unbinder unbinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_agreement, container, false);
    unbinder = ButterKnife.bind(this, view);
    loadingPanel.setText("Загрузка соглашения...");
    webView.setVisibility(View.GONE);
    loadingPanel.setVisibility(View.VISIBLE);

    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        loadingPanel.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        super.onPageFinished(view, url);
      }
    });
    webView.setWebChromeClient(new WebChromeClient());
    webView.setBackgroundColor(Color.WHITE);
    WebSettings webSettings = webView.getSettings();
    webSettings.setSupportZoom(true);
    webSettings.setBuiltInZoomControls(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDefaultTextEncodingName("utf-8");

    webView.loadUrl("http://bil24.pro/agreement.html?rnd=" + System.currentTimeMillis());
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }
}