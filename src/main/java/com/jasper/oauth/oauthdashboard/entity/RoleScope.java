package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "role_scope")
@Data
public class RoleScope {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "role_id", nullable = false)
  private int roleId;

  @Column(name = "scope_id", nullable = false)
  private int scopeId;

  @Column(name = "created_by", nullable = false)
  private int createdBy;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;
}
