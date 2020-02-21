package com.bil24.fragments.action.filter;

/**
 * Created by SVV on 12.05.2016
 */
public class Kind {
  private long kindId;
  private String kindName;

  public Kind(long kindId, String kindName) {
    this.kindId = kindId;
    this.kindName = kindName;
  }

  public long getKindId() {
    return kindId;
  }

  public String getKindName() {
    return kindName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Kind)) return false;
    Kind order = (Kind) o;
    return kindId == order.getKindId();
  }

  @Override
  public int hashCode() {
    return (int) (kindId ^ (kindId >>> 32));
  }

  public static class Type {
    public static final int MEC = 1;
  }
}
