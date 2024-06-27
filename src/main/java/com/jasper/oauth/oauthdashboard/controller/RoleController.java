package com.jasper.oauth.oauthdashboard.controller;

import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.Result;
import com.jasper.oauth.oauthdashboard.model.role.RoleCreateRequest;
import com.jasper.oauth.oauthdashboard.model.role.RoleUpdateRequest;
import com.jasper.oauth.oauthdashboard.service.RoleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/api/role/")
public class RoleController {
  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @GetMapping("v1/list")
  public Object list(PageParam pageParam) {
    try {
      return Result.success(roleService.getRoleList(pageParam));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @GetMapping("v1/{roleId}/detail")
  public Object detail(@PathVariable("roleId") Integer roleId) {
    try {
      return Result.success(roleService.getRoleDetail(roleId));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @PostMapping("v1/create")
  public Object createRole(@RequestBody RoleCreateRequest request) {
    try {
      roleService.createRole(request);
      return Result.success();
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @PutMapping("v1/{roleId}/update")
  public Object update(
      @PathVariable(name = "roleId") Integer roleId, @RequestBody RoleUpdateRequest request) {
    try {
      roleService.updateRole(roleId, request);
      return Result.success();
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @DeleteMapping("v1/{roleId}/delete")
  public Object delete(@PathVariable(name = "roleId") Integer roleId) {
    try {
      roleService.deleteRole(roleId);
      return Result.success();
    } catch (Exception e) {
      return Result.fail(e);
    }
  }
}
