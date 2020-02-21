package com.bil24.adapter;

import android.content.Context;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import com.bil24.Controller;
import com.bil24.fragments.InFragment;
import com.bil24.fragments.action.*;
import com.bil24.fragments.action.filter.Kind;
import com.bil24.storage.sql.DataBase;
import server.net.obj.extra.ExtraActionV2;

/**
 * User: SVV
 * Date: 07.06.2015.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements PagerListener {
  private static InFragment inFragment;
  private ViewPager pager;
  private int count = 1;
  private String currentActionVenue = "";
  private Context context;

  public MyFragmentPagerAdapter(FragmentManager mFragmentManager, Context context) {
    super(mFragmentManager);
    this.context = context;
    Controller.getInstance().setPagerListener(this);
  }

  public void setPager(ViewPager pager) {
    this.pager = pager;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return new ActionFragment();
      case 1:
        return new InFragment();
    }
    return null;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
    switch (position) {
      case 0:
        break;
      case 1:
        inFragment = (InFragment) createdFragment;
        break;
    }
    return createdFragment;
  }

  @Override
  public int getCount() {
    return count;
  }

  @Override
  public void openPage1(boolean deleteTwoPage) {
    pager.setCurrentItem(0);
    if (deleteTwoPage) {
      count = 1;
      currentActionVenue = "";
      notifyDataSetChanged();
    }
  }

  @Override
  public void openPage2(ExtraActionV2 action) {
    if (count == 1) {
      count = 2;
      notifyDataSetChanged();
      pager.setCurrentItem(1);
    }

    pager.setCurrentItem(1);
    DataBase db = DataBase.getInstance(context);
    String newActionVenue = db.city.getSelectedCityId() + "_" + db.venue.getSelectedVenueId() + "_" + action.getActionId();
    if (!currentActionVenue.equals(newActionVenue)) {
      if (action.getKindId() == Kind.Type.MEC) {
        EventSeatMecFragment eventSeatMecFragment = EventSeatMecFragment.netInstance(action);
        inFragment.showFragment(eventSeatMecFragment, newActionVenue);
      } else {
        EventSeatFragment eventSeatFragment = EventSeatFragment.newInstance(action);
        inFragment.showFragment(eventSeatFragment, newActionVenue);
      }
    }
    currentActionVenue = newActionVenue;
  }

  @Override
  public int getPage() {
    return pager.getCurrentItem();
  }
}