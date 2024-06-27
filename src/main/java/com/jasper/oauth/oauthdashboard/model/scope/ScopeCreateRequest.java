package com.jasper.oauth.oauthdashboard.model.scope;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ScopeCreateRequest {
  @NotBlank(message = "Resource code is required")
  private String resourceCode;
  @NotBlank(message = "Scope code is required")
  private String scopeCode;
  private String label;
}
