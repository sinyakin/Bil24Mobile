package com.bil24.adapter.promo;

import android.support.annotation.NonNull;

import server.net.obj.extra.ExtraPromoCode;
import server.net.obj.extra.ExtraPromoPack;

public class ExtraPromo {
  private ExtraPromoPack extraPromoPack;
  private ExtraPromoCode extraPromoCode;

  public ExtraPromo(@NonNull ExtraPromoPack extraPromoPack, @NonNull ExtraPromoCode extraPromoCode) {
    this.extraPromoPack = extraPromoPack;
    this.extraPromoCode = extraPromoCode;
  }

  @NonNull
  public ExtraPromoPack getExtraPromoPack() {
    return extraPromoPack;
  }

  @NonNull
  public ExtraPromoCode getExtraPromoCode() {
    return extraPromoCode;
  }
}
