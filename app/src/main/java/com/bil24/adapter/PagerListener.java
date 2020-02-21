package com.bil24.adapter;

import server.net.obj.extra.ExtraActionV2;

/**
 * User: SVV
 * Date: 07.06.2015.
 */
public interface PagerListener {
  void openPage1(boolean deleteTwoPage);
  void openPage2(ExtraActionV2 actionRoot);
  int getPage();
}
