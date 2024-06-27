package com.jasper.oauth.oauthdashboard.model.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class ClientCreateAndUpdateRequest {
  @NotBlank(message = "clientCode must not be blank")
  private String clientCode;
  @NotBlank(message = "clientSecret must not be blank")
  private String clientSecret;
  private String webServerRedirectUri;
  private String serviceCode;
  private String contentCode;
  @Min(value = 1, message = "accessTokenValidity must be greater than 0")
  private Long accessTokenValidity;
  @Min(value = 1, message = "refreshTokenValidity must be greater than 0")
  private Long refreshTokenValidity;
  private String additionalInformation = "{}";
  private String systemCode;
  @Size.List({
    @Size(min = 1, message = "grantTypeList must not be empty"),
  })
  private List<String> grantTypeList;
  private List<String> resourceCodeList;
  private List<String> scopeCodeList;

}
