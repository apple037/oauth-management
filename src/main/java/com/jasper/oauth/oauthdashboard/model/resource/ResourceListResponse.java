package com.jasper.oauth.oauthdashboard.model.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceListResponse {
  private Integer id;
  private String code;
  private String label;
  private int clientCount;
}
