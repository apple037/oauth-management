package com.jasper.oauth.oauthdashboard.model.resource;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceDetailResponse {
  private Integer id;
  private String code;
  private String label;
  private String description;
  private List<String> clientCodeList;
  private Integer createdBy;
  private LocalDateTime createdDate;
  private Integer updatedBy;
  private LocalDateTime updatedDate;
}
