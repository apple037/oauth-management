package com.jasper.oauth.oauthdashboard.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "role")
@Data
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "code", nullable = false, length = 50)
  private String code;

  @Column(name = "label", nullable = false, length = 100)
  private String label;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_by", columnDefinition = "int default 0")
  private Integer lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;
}
