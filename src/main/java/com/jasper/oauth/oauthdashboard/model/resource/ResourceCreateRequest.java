package com.jasper.oauth.oauthdashboard.model.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceCreateRequest {
  @NotBlank(message = "Resource code is required")
  private String code;
  @NotBlank(message = "Resource label is required")
  private String label;
  @Length(max = 300, message = "Resource description length exceeds 300 characters")
  private String description;
}
