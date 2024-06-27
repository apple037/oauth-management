package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "oauth_client")
public class OauthClient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "client_code", nullable = false, length = 50)
  private String clientCode;

  @Column(name = "client_secret", nullable = false, length = 255)
  private String clientSecret;

  @Column(name = "web_server_redirect_url", nullable = false, length = 200)
  private String webServerRedirectUrl;

  @Column(name = "service_code", nullable = false, length = 50)
  private String serviceCode;

  @Column(name = "content_code", length = 50)
  private String contentCode;

  @Column(name = "access_token_validity")
  private Integer accessTokenValidity;

  @Column(name = "refresh_token_validity")
  private Integer refreshTokenValidity;

  @Column(name = "additional_information", length = 4000)
  private String additionalInformation;

  @Column(name = "auto_approve", length = 300)
  private String autoApprove;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;

  @Column(name = "system_code", nullable = false, length = 50)
  private String systemCode;

}


