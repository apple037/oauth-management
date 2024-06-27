package com.jasper.oauth.oauthdashboard.model.account;

import java.util.Set;
import lombok.Data;

@Data
public class CheckPermissionResponse {
  private boolean hasPermission;
  private boolean isResourceMatched;
  private Set<String> matchedScopes;
  private Set<String> missingScopes;
  private Set<String> matchedRoles;
  private Set<String> missingRoles;
}
