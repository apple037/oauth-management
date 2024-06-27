package com.jasper.oauth.oauthdashboard.model.role;

import lombok.Data;

@Data
public class RoleListResponse {
  private Integer id;
  private String code;
  private String label;
  private int accountCount;
}
