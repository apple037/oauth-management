package com.jasper.oauth.oauthdashboard.controller;

import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.Result;
import com.jasper.oauth.oauthdashboard.model.scope.ScopeCreateRequest;
import com.jasper.oauth.oauthdashboard.service.ScopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Scope", description = "Scope management")
@RestController
@RequestMapping("/oauth/api/scope")
public class ScopeController {
  private final ScopeService scopeService;

  public ScopeController(ScopeService scopeService) {
    this.scopeService = scopeService;
  }

  @Operation(summary = "Get scope list", description = "Get scope list in pages")
  @GetMapping("v1/list")
  public Object getScopeList(PageParam pageParam) {
    try {
      return Result.success(scopeService.getScopeList(pageParam));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Get scope detail", description = "Get scope detail by scope id")
  @GetMapping("v1/{scopeId}/detail")
  public Object getScopeDetail(@PathVariable(name = "scopeId") Integer scopeId) {
    try {
      return Result.success(scopeService.getScopeDetail(scopeId));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Create scope", description = "Create scope")
  @PostMapping("v1/create")
  public Object createScope(@RequestBody ScopeCreateRequest request) {
    try {
      return Result.success(scopeService.createScope(request));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }
}
