package com.bil24.menu;

/**
 * Created by SVV on 14.09.2015
 */
public interface UpdateLeftMenuListener {
  void updateLeftMenuBasketAmount(int amount);
  void updateLeftMenuTicketAmount();
  void updateLeftMenuMECsAmount();
  void updateLeftMenuOrderAmount(int amount);
  void updateLeftMenuPromoCodesAmount(int amount);
  void resetLeftMenu();
}
