package com.jasper.oauth.oauthdashboard.model;

import com.jasper.oauth.oauthdashboard.constant.MsgCodeEnum;
import com.jasper.oauth.oauthdashboard.constant.MsgLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
  private String msgLevel;
  private String msgCode;
  private String msg;
  private T data;

  public static Result<String> success() {
    return Result.<String>builder()
        .msgLevel(MsgLevelEnum.SUCCESS.getLevel())
        .msgCode(MsgCodeEnum.SUCCESS.getCode())
        .msg("Success")
        .data("")
        .build();
  }

  public static <T>Result<T> success(T data) {
    return Result.<T>builder()
        .msgLevel(MsgLevelEnum.SUCCESS.getLevel())
        .msgCode(MsgCodeEnum.SUCCESS.getCode())
        .msg("Success")
        .data(data)
        .build();
  }

  public static <T> Result<T> customSuccess(MsgLevelEnum msgLevel, String msgCode, String msg, T data) {
    return Result.<T>builder()
        .msgLevel(msgLevel.getLevel())
        .msgCode(msgCode)
        .msg(msg)
        .data(data)
        .build();
  }

  public static Result<String> fail(Exception e) {
    return Result.<String>builder()
        .msgLevel(MsgLevelEnum.ERROR.getLevel())
        .msgCode(MsgCodeEnum.SYSTEM_ERROR.getCode())
        .msg("Fail")
        .data(e.getMessage())
        .build();
  }

  public Result<String> customFail(String msgLevel, String msgCode, String msg, Exception e) {
    return Result.<String>builder()
        .msgLevel(msgLevel)
        .msgCode(msgCode)
        .msg(msg)
        .data(e.getMessage())
        .build();
  }
}
