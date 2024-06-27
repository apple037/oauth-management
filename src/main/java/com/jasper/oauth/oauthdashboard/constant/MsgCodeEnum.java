package com.jasper.oauth.oauthdashboard.constant;

import lombok.Getter;

@Getter
public enum MsgCodeEnum {
  SUCCESS("00000000"),
  SYSTEM_ERROR("99999999"),
  CUSTOM_ERROR("99999998"),
  ;

  MsgCodeEnum(String code) {
    this.code = code;
  }

  private final String code;
}
