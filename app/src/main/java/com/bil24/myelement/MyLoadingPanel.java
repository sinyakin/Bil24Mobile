package com.bil24.myelement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import com.bil24.R;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.TextView;

/**
 * Виктор
 * 10.08.2015.
 */
public class MyLoadingPanel extends RelativeLayout {

  private LayoutInflater mInflater;
  private TextView tv;

  public MyLoadingPanel(Context context) {
    super(context);
    mInflater = LayoutInflater.from(context);
    init();
  }

  public MyLoadingPanel(Context context, AttributeSet attrs) {
    super(context, attrs);
    mInflater = LayoutInflater.from(context);
    init();
  }

  public MyLoadingPanel(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mInflater = LayoutInflater.from(context);
    init();
  }

  private void init() {
    mInflater.inflate(R.layout.loadin_panel, this, true);
    tv = (TextView) findViewById(R.id.textView);
    tv.setText("Загрузка данных...");
  }

  public void setTextColor(int color) {
    tv.setTextColor(color);
  }

  public void goneTextView() {
    tv.setVisibility(GONE);
  }

  public void setText(@NonNull String text) {
    tv.setText(text);
  }
}
