package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_client_grant_types")
@Data
public class OauthClientGrantTypes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "client_id", nullable = false)
  private int clientId;

  @Column(name = "grant_type", nullable = false, length = 50)
  private String grantType;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;
}
