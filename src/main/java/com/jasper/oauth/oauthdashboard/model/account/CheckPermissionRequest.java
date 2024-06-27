package com.jasper.oauth.oauthdashboard.model.account;

import java.util.Set;
import lombok.Data;

@Data
public class CheckPermissionRequest {
  // Client side information
  private String account;
  private String clientCode;
  // Server side information
  private String resourceCode;
  private Set<String> expectedScopes;
  private Set<String> expectedRoles;
}
