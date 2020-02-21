package com.bil24.myelement;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * User: SVV
 * Date: 29.05.2015.
 */
//у стандартной баг. первый раз не отображается загрузка
public class MySwipeRefreshLayout extends SwipeRefreshLayout {

  private boolean mMeasured = false;
  private boolean mPreMeasureRefreshing = false;

  public MySwipeRefreshLayout(Context context) {
    super(context);
  }

  public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (!mMeasured) {
      mMeasured = true;
      setRefreshing(mPreMeasureRefreshing);
    }
  }

  @Override
  public void setRefreshing(boolean refreshing) {
    if (mMeasured) {
      super.setRefreshing(refreshing);
    } else {
      mPreMeasureRefreshing = refreshing;
    }
  }
}
