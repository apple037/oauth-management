package com.jasper.oauth.oauthdashboard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "resource")
@Data
public class Resource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "code", nullable = false, length = 50)
  private String code;

  @Column(name = "label", nullable = false, length = 45)
  private String label;

  @Column(name = "description", length = 300)
  private String description;

  @Column(name = "created_by", nullable = false)
  private int createdBy;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;
}
