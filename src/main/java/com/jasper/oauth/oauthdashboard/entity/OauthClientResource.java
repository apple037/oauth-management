package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "oauth_client_resource")
@Data
public class OauthClientResource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "client_id", nullable = false)
  private int clientId;

  @Column(name = "resource_id", nullable = false)
  private int resourceId;

  @Column(name = "created_by", nullable = false)
  private int createdBy;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;
}
