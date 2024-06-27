package com.jasper.oauth.oauthdashboard.model.client;

import java.util.List;
import lombok.Data;

@Data
public class ClientDetailResponse {
  private Integer id;
  private String code;
  private String serviceCode;
  private String systemCode;
  private Long accessTokenValidity;
  private Long refreshTokenValidity;
  private String additionalInformation;
  private Integer createdBy;
  private String createdDate;
  private Integer lastModifiedBy;
  private String lastModifiedDate;
  private List<String> grantTypeList;
  private List<String> resourceCodeList;
  private List<String> scopeCodeList;
}
