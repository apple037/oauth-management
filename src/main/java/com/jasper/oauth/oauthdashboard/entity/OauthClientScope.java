package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "oauth_client_scope")
@Data
public class OauthClientScope {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "oauth_client_id", nullable = false)
  private int oauthClientId;

  @Column(name = "scope_id", nullable = false)
  private int scopeId;

  @Column(name = "created_by", nullable = false, columnDefinition = "int default 0")
  private int createdBy;

  @Column(
      name = "created_date",
      nullable = false,
      columnDefinition = "datetime default CURRENT_TIMESTAMP")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by", columnDefinition = "int default 0")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date", columnDefinition = "datetime default CURRENT_TIMESTAMP")
  private LocalDateTime lastModifiedDate;
}
