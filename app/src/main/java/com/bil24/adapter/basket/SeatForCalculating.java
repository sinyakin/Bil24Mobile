package com.bil24.adapter.basket;

import java.math.BigDecimal;

/**
 * Created by SVV on 26.10.2016
 *
 * Класс сделан для того, чтобы при расчете сумм не использовать
 * классы, которые переданы в адаптер, иначе
 * поведение адаптера непредсказуемо
 */

public class SeatForCalculating {
  private long id;
  private BigDecimal sum;
  private BigDecimal charge;
  private int kind;
  private boolean checked = true;

  public SeatForCalculating(long id, BigDecimal sum, BigDecimal charge, int kind) {
    this.id = id;
    this.sum = sum;
    this.charge = charge;
    this.kind = kind;
  }

  public long getId() {
    return id;
  }

  public BigDecimal getSum() {
    return sum;
  }

  public BigDecimal getCharge() {
    return charge;
  }

  public int getKind() {
    return kind;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }
}
