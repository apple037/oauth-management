package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "scope")
@Data
public class Scope {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "resource_id", nullable = false)
  private int resourceId;

  @Column(name = "scope_code", length = 50)
  private String scopeCode;

  @Column(name = "label", length = 100)
  private String label;

  @Column(name = "created_by", nullable = false)
  private int createdBy;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;
}
