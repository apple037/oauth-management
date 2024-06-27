package com.jasper.oauth.oauthdashboard.model.account;

import java.util.List;
import lombok.Data;

@Data
public class AccountCreateRequest {
  private String username;
  private String password;
  private Boolean enabled;
  private Boolean locked;
  private List<String> roles;
  private List<String> additionalScopes;
}
