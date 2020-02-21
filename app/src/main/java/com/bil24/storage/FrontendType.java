package com.bil24.storage;

/**
 * Created by SVV on 16.08.2016
 */
public enum FrontendType {
  UNKNOWN(0, ""), ANDROID(1, ""), TICKET_OFFICE(6, "МРМК"), INVITATION(7, "П-МРМК");

  int id;
  String desc;

  FrontendType(int id, String desc) {
    this.id = id;
    this.desc = desc;
  }

  public int getId() {
    return id;
  }

  public String getDesc() {
    return desc;
  }

  public static FrontendType get(Integer id) {
    if (id == null) return UNKNOWN;
    for (FrontendType frontendType : values()) {
      if (frontendType.getId() == id) return frontendType;
    }
    return UNKNOWN;
  }
}
