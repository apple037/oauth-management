package com.jasper.oauth.oauthdashboard.model.account;

import java.util.List;
import lombok.Data;

@Data
public class SimulateLoginResponse {
  private Integer accountId;
  private String userName;
  private String clientCode;
  private List<String> resourceCodeList;
  private List<String> roleCodeList;
  private List<String> scopeCodeList;
}
