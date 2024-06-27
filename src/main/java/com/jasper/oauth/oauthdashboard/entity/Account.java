package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain.Strategy;

@Data
@Entity
@Table(name = "account")
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "user_name")
  private String userName;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "expired", columnDefinition = "TINYINT")
  private boolean expired;

  @Column(name = "locked", columnDefinition = "TINYINT")
  private Boolean locked;

  @Column(name = "enabled", columnDefinition = "TINYINT")
  private Boolean enabled;

  @Column(name = "credentials_expired", columnDefinition = "TINYINT")
  private Boolean credentialsExpired;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;

  @Column(name = "additional_scopes")
  private String additionalScopes;
}
