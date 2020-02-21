package com.bil24.adapter.basket;

import android.support.annotation.NonNull;

import java.util.List;

import server.net.obj.extra.ExtraActionEventCart;

public interface ListViewHeightListener {
  void change(ExtraActionEventCart actionEvent, @NonNull List<Long> seatIdList);
}
