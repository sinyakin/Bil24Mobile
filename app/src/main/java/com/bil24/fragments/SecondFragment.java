package com.bil24.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import com.bil24.*;

import butterknife.Unbinder;

/**
 * Created by SVV on 31.10.2016
 */

public abstract class SecondFragment extends Fragment {
  private static boolean fromSecondFragment = false;
  private MenuHideListener menuHideListener;
  private View.OnClickListener defaultActionBarListener;
  public Unbinder unbinder;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getActivity() instanceof MenuHideListener) {
      menuHideListener = (MenuHideListener) getActivity();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @Override
  public void onPause() {
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      if (Bil24Application.withMiniDrawer()) actionBar.setDisplayHomeAsUpEnabled(false);
      if (menuHideListener != null) menuHideListener.onShowMenu();
    }
    super.onPause();
  }

  @Override
  public void onResume() {
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      /*if (Bil24Application.withMiniDrawer()) */
      actionBar.setHomeAsUpIndicator(ActionBar.NAVIGATION_MODE_STANDARD);
      actionBar.setDisplayHomeAsUpEnabled(true);
      if (menuHideListener != null) menuHideListener.onHideMenu();
    }
    fromSecondFragment = true;
    super.onResume();
  }

  public static boolean isFromSecondFragment() {
    boolean tmp = fromSecondFragment;
    if (tmp) fromSecondFragment = false;
    return tmp;
  }

  public interface MenuHideListener {
    void onShowMenu();
    void onHideMenu();
  }
}
