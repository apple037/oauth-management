package com.jasper.oauth.oauthdashboard.controller;

import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.Result;
import com.jasper.oauth.oauthdashboard.model.resource.ResourceCreateRequest;
import com.jasper.oauth.oauthdashboard.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resource", description = "Resource management")
@RestController
@RequestMapping("oauth/api/resource/")
public class ResourceController {
  private final ResourceService resourceService;

  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @Operation(summary = "Get resource list", description = "Get resource list in pages")
  @GetMapping("v1/list")
  public Object getResourceList(PageParam pageParam) {
    try {
      return Result.success(resourceService.getResourceList(pageParam));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Get resource detail", description = "Get resource detail by resource id")
  @GetMapping("v1/{resourceId}/detail")
  public Object getResourceDetail(@PathVariable(name = "resourceId") Integer resourceId) {
    try {
      return Result.success(resourceService.getResourceDetail(resourceId));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Create resource", description = "Create resource")
  @PostMapping("v1/create")
  public Object createResource(@RequestBody @Valid ResourceCreateRequest request) {
    try {
      return Result.success(resourceService.createResource(request));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }
}
