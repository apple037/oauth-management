package com.jasper.oauth.oauthdashboard.model.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountDeleteRequest {
  @JsonProperty("isHardDelete")
  private boolean isHardDelete;
}
