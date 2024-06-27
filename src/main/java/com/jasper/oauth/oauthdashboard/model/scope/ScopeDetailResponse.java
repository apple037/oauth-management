package com.jasper.oauth.oauthdashboard.model.scope;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ScopeDetailResponse {
  private Integer id;
  private String resourceCode;
  private String scopeCode;
  private String label;
  private List<String> clientCodeList;
  private List<String> roleCodeList;
  private List<String> accountList;
  private Integer createdBy;
  private LocalDateTime createdDate;
  private Integer updatedBy;
  private LocalDateTime updatedDate;
}
