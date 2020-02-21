package com.bil24.adapter.basket;

import java.util.List;

/**
 * Виктор
 * 12.08.2015.
 */
public interface BasketListener {
  void changeSeatChecked(List<Long> seatIdList, boolean checked, boolean discount);
  void blockInterface();
  void unBlockInterface();
}
