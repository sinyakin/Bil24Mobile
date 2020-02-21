package com.bil24.menu;

import android.graphics.Color;
import android.support.annotation.Nullable;
import com.bil24.*;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * User: SVV
 * Date: 29.05.2015.
 */
public enum LeftMenu {
  ACTIONS(R.string.drawer_item_events) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(FontAwesome.Icon.faw_th).
          withSelectedIconColor(Color.BLACK).
          withIdentifier(ordinal());
    }
  },

  BASKET(R.string.drawer_item_basket) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(R.mipmap.ic_menu_basket).
          withSelectedIcon(R.mipmap.ic_menu_basket_selected).
          withBadgeStyle(badgeStyle).
          withIdentifier(ordinal());
    }
  },

  MY_ORDERS(R.string.drawer_item_my_orders) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(R.mipmap.ic_menu_orders).
          withSelectedIcon(R.mipmap.ic_menu_orders_selected).
          withBadgeStyle(badgeStyle).
          withIdentifier(ordinal());
    }
  },

  MY_TICKETS(R.string.drawer_item_my_tickets) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(R.mipmap.ic_menu_ticket).
          withSelectedIcon(R.mipmap.ic_menu_ticket_selected).
          withBadgeStyle(badgeStyle).
          withIdentifier(ordinal());
    }
  },

  MY_MECS(R.string.drawer_item_my_cards) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(R.mipmap.ic_menu_card).
          withSelectedIcon(R.mipmap.ic_menu_card_selected).
          withBadgeStyle(badgeStyle).
          withIdentifier(ordinal());
    }
  },

  MY_PROMO_CODES(R.string.drawer_item_my_promo_codes) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(R.mipmap.ic_menu_promo_codes).
          withSelectedIcon(R.mipmap.ic_menu_promo_codes_selected).
          withBadgeStyle(badgeStyle).
          withIdentifier(ordinal());
    }
  },

  NEWS(R.string.drawer_item_news) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(FontAwesome.Icon.faw_newspaper_o).
          withSelectedIconColor(Color.BLACK).
          withSelectedColor(Color.WHITE).
          withIdentifier(ordinal());
    }
  },

  DIVIDER(0) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new DividerDrawerItem();
    }
  },

  SETTING(R.string.action_settings) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(FontAwesome.Icon.faw_cog).
          withSelectedIconColor(Color.BLACK).
          withIdentifier(ordinal());
    }
  },

  AGREEMENT(R.string.drawer_item_agreement) {
    @Override
    public IDrawerItem getiDrawerItem() {
      return new PrimaryDrawerItem().
          withName(getDesc()).
          withIcon(FontAwesome.Icon.faw_info).
          withSelectedIconColor(Color.BLACK).
          withIdentifier(ordinal());
    }
  };

  private static BadgeStyle badgeStyle = new BadgeStyle(
      com.mikepenz.materialdrawer.R.drawable.material_drawer_badge,
      Bil24Application.getContext().getResources().getColor(R.color.toolbar_lite),
      Bil24Application.getContext().getResources().getColor(R.color.toolbar_lite), Color.WHITE);

  private int desc;

  LeftMenu(int desc) {
    this.desc = desc;
  }

  public int getDesc() {
    return desc;
  }

  public abstract IDrawerItem getiDrawerItem();

  @Nullable
  public static LeftMenu get(long ordinal) {
    for (LeftMenu leftMenu : LeftMenu.values()) {
      if (leftMenu.ordinal() == ordinal) return leftMenu;
    }
    return null;
  }
}
