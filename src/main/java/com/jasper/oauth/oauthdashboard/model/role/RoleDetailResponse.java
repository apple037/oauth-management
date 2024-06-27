package com.jasper.oauth.oauthdashboard.model.role;

import com.jasper.oauth.oauthdashboard.model.scope.ScopeDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class RoleDetailResponse {
  private Integer id;
  private String code;
  private String label;
  private Integer createdBy;
  private LocalDateTime createdDate;
  private Integer updatedBy;
  private LocalDateTime updatedDate;
  private List<ScopeDto> scopes;
  private int accountCount;
}
