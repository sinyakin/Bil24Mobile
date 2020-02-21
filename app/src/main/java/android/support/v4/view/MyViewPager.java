package android.support.v4.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * User: SVV
 * Date: 07.06.2015.
 */
public class MyViewPager extends ViewPager {

  public MyViewPager(Context context) {
    super(context);

  }
  public MyViewPager(Context context, AttributeSet attr) {
    super(context,attr);

  }

  @Override
  void smoothScrollTo(int x, int y, int velocity) {
    super.smoothScrollTo(x, y, 1);
  }

}