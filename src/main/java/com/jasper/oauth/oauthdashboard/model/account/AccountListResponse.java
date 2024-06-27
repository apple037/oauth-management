package com.jasper.oauth.oauthdashboard.model.account;

import lombok.Data;

@Data
public class AccountListResponse {
  private int id;
  private String username;
  private int roleCount;
  private boolean enabled;
  private boolean locked;
}
