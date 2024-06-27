package com.jasper.oauth.oauthdashboard.model.scope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScopeDto {
  private Integer id;
  private String scopeCode;
  private String scopeLabel;
  private String resourceCode;
  private String resourceDesc;
}
