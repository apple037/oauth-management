package com.jasper.oauth.oauthdashboard.model;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
public class PageParam {
  private int size = 10;
  private int page = 1;

  public Pageable toPageable() {
    return PageRequest.of(page - 1, size);
  }
}
