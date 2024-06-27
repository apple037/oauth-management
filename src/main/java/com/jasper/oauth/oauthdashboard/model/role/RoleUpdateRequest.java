package com.jasper.oauth.oauthdashboard.model.role;

import java.util.Set;
import lombok.Data;

@Data
public class RoleUpdateRequest {
  private String code;
  private String label;
  private Set<String> scopeCodes;
}
