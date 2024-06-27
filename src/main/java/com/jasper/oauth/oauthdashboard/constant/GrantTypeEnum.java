package com.jasper.oauth.oauthdashboard.constant;

import lombok.Getter;

@Getter
public enum GrantTypeEnum {
  PASSWORD("password"),
  REFRESH_TOKEN("refresh_token"),
  ;

  GrantTypeEnum(String type) {
    this.type = type;
  }

  private final String type;

  public static GrantTypeEnum fromString(String text) {
    for (GrantTypeEnum b : GrantTypeEnum.values()) {
      if (b.type.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }

  public static boolean contains(String text) {
    for (GrantTypeEnum b : GrantTypeEnum.values()) {
      if (b.type.equalsIgnoreCase(text)) {
        return true;
      }
    }
    return false;
  }
}
