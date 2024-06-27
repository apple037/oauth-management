package com.jasper.oauth.oauthdashboard.constant;

import lombok.Getter;

@Getter
public enum MsgLevelEnum {
  SUCCESS("success"),
  INFO("info"),
  WARN("warn"),
  ERROR("error"),
  FINE("fine"),
  ;

  MsgLevelEnum(String level) {
    this.level = level;
  }

  private final String level;
}
