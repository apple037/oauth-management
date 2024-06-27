package com.jasper.oauth.oauthdashboard.model.account;

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
public class AccountDetailResponse {
  private Integer id;
  private String username;
  private boolean enabled;
  private boolean locked;
  private List<String> roles;
  private List<String> additionalScopes;
  private List<String> roleScopes;
  private List<String> availableScopes;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
}
