package com.jasper.oauth.oauthdashboard.model.scope;

import lombok.Data;

@Data
public class ScopeListResponse {
  private Integer id;
  private String resourceName;
  private String scopeCode;
  private String label;
  // 計算有多少 role 和 client 使用這個 scope
  private int roleScopeCount;
  private int clientScopeCount;
  // 查詢 account 中的 additional_information中
  private int userScopeCount;
}
