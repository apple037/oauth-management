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
@Table(name = "account_role")
public class AccountRole {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;
  @Column(name = "account_id")
  private Integer accountId;
  @Column(name = "role_id")
  private Integer roleId;
  @Column(name = "created_by")
  private Integer createdBy;
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;
  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;
}
