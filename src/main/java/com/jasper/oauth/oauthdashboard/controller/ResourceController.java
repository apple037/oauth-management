package com.jasper.oauth.oauthdashboard.controller;

import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.Result;
import com.jasper.oauth.oauthdashboard.model.resource.ResourceCreateRequest;
import com.jasper.oauth.oauthdashboard.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("oauth/api/resource/")
public class ResourceController {
  private final ResourceService resourceService;

  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @GetMapping("v1/list")
  public Object getResourceList(PageParam pageParam) {
    try {
      return Result.success(resourceService.getResourceList(pageParam));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @GetMapping("v1/{resourceId}/detail")
  public Object getResourceDetail(@PathVariable(name = "resourceId") Integer resourceId) {
    try {
      return Result.success(resourceService.getResourceDetail(resourceId));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @PostMapping("v1/create")
  public Object createResource(@RequestBody @Valid ResourceCreateRequest request) {
    try {
      return Result.success(resourceService.createResource(request));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }
}
