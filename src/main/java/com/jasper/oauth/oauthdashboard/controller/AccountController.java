package com.jasper.oauth.oauthdashboard.controller;

import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.Result;
import com.jasper.oauth.oauthdashboard.model.account.AccountCreateRequest;
import com.jasper.oauth.oauthdashboard.model.account.AccountDeleteRequest;
import com.jasper.oauth.oauthdashboard.model.account.AccountDetailResponse;
import com.jasper.oauth.oauthdashboard.model.account.AccountListResponse;
import com.jasper.oauth.oauthdashboard.model.account.AccountUpdateRequest;
import com.jasper.oauth.oauthdashboard.model.account.CheckPermissionRequest;
import com.jasper.oauth.oauthdashboard.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account", description = "Account management")
@RestController
@RequestMapping(value = "oauth/api/account")
public class AccountController {
  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @Operation(summary = "Get account list", description = "Get account list in pages")
  @GetMapping("v1/list")
  public Object getAccountList(PageParam pageParam) {
    try {
      Page<AccountListResponse> accountPages = accountService.getAccountList(pageParam);
      return Result.success(accountPages);
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Get account detail", description = "Get account detail by account id")
  @GetMapping("v1/{accountId}/detail")
  public Object getAccountDetail(@PathVariable("accountId") Integer accountId) {
    try {
      AccountDetailResponse response = accountService.getAccountDetail(accountId);
      return Result.success(response);
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Update account", description = "Update account by account id")
  @PutMapping("v1/{accountId}/update")
  public Object updateAccount(
      @PathVariable("accountId") Integer accountId, @RequestBody AccountUpdateRequest request) {
    try {
      accountService.updateAccount(accountId, request);
      return Result.success();
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Create account", description = "Create account")
  @PostMapping("v1/create")
  public Object createAccount(@RequestBody AccountCreateRequest request) {
    try {
      return Result.success(accountService.createAccount(request));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @DeleteMapping("v1/{accountId}/delete")
  public Object deleteAccount(
      @PathVariable("accountId") Integer accountId, @RequestBody AccountDeleteRequest request) {
    try {
      accountService.deleteAccount(accountId, request);
      return Result.success();
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Simulate login", description = "Simulate login by account and client code")
  @GetMapping("v1/simulateLogin")
  public Object simulateLogin(String account, String clientCode) {
    try {
      return Result.success(accountService.simulateLogin(account, clientCode));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(
      summary = "Check permission",
      description = "Check permission by account, client code, resource code and scopes")
  @PostMapping("v1/checkPermission")
  public Object checkPermission(@RequestBody CheckPermissionRequest request) {
    try {
      return Result.success(accountService.checkPermission(request));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }
}
