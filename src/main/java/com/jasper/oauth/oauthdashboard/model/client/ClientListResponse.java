package com.jasper.oauth.oauthdashboard.model.client;

import lombok.Data;

@Data
public class ClientListResponse {
  private Integer id;
  private String code;
  private int resourceCount;
  private int scopeCount;
}
