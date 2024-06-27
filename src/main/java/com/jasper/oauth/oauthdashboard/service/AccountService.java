package com.jasper.oauth.oauthdashboard.service;

import com.jasper.oauth.oauthdashboard.entity.Account;
import com.jasper.oauth.oauthdashboard.entity.AccountRole;
import com.jasper.oauth.oauthdashboard.entity.OauthClient;
import com.jasper.oauth.oauthdashboard.entity.OauthClientResource;
import com.jasper.oauth.oauthdashboard.entity.OauthClientScope;
import com.jasper.oauth.oauthdashboard.entity.Resource;
import com.jasper.oauth.oauthdashboard.entity.Role;
import com.jasper.oauth.oauthdashboard.entity.RoleScope;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.account.AccountCreateRequest;
import com.jasper.oauth.oauthdashboard.model.account.AccountDeleteRequest;
import com.jasper.oauth.oauthdashboard.model.account.AccountDetailResponse;
import com.jasper.oauth.oauthdashboard.model.account.AccountListResponse;
import com.jasper.oauth.oauthdashboard.model.account.AccountUpdateRequest;
import com.jasper.oauth.oauthdashboard.model.account.CheckPermissionRequest;
import com.jasper.oauth.oauthdashboard.model.account.CheckPermissionResponse;
import com.jasper.oauth.oauthdashboard.model.account.SimulateLoginResponse;
import com.jasper.oauth.oauthdashboard.service.dataaccess.AccountDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.AccountRoleDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.RoleDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.RoleScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.utils.SecretUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class AccountService {
  private final AccountDataAccessService accountDataAccessService;
  private final AccountRoleDataAccessService accountRoleDataAccessService;
  private final RoleDataAccessService roleDataAccessService;
  private final ScopeDataAccessService scopeDataAccessService;
  private final RoleScopeDataAccessService roleScopeDataAccessService;
  private final ClientDataAccessService clientDataAccessService;
  private final ClientScopeDataAccessService clientScopeDataAccessService;
  private final ClientResourceDataAccessService clientResourceDataAccessService;
  private final ResourceDataAccessService resourceDataAccessService;

  public AccountService(
      AccountDataAccessService accountDataAccessService,
      AccountRoleDataAccessService accountRoleDataAccessService,
      RoleDataAccessService roleDataAccessService,
      ScopeDataAccessService scopeDataAccessService,
      RoleScopeDataAccessService roleScopeDataAccessService,
      ClientDataAccessService clientDataAccessService,
      ClientScopeDataAccessService clientScopeDataAccessService,
      ClientResourceDataAccessService clientResourceDataAccessService,
      ResourceDataAccessService resourceDataAccessService) {
    this.accountDataAccessService = accountDataAccessService;
    this.accountRoleDataAccessService = accountRoleDataAccessService;
    this.roleDataAccessService = roleDataAccessService;
    this.scopeDataAccessService = scopeDataAccessService;
    this.roleScopeDataAccessService = roleScopeDataAccessService;
    this.clientDataAccessService = clientDataAccessService;
    this.clientScopeDataAccessService = clientScopeDataAccessService;
    this.clientResourceDataAccessService = clientResourceDataAccessService;
    this.resourceDataAccessService = resourceDataAccessService;
  }

  // Get account list
  public Page<AccountListResponse> getAccountList(PageParam pageParam) {
    log.debug("[AccountService] getAccountList()");
    Pageable pageable = pageParam.toPageable();
    Page<Account> accountPages = accountDataAccessService.findAll(pageable);
    List<Account> accountList = accountPages.getContent();
    List<AccountRole> roleList =
        accountRoleDataAccessService.findAllByAccountIdIn(
            accountList.stream().map(Account::getId).toList());
    HashMap<Integer, Integer> accountRoleCountMap =
        roleList.stream()
            .collect(
                HashMap::new,
                (map, accountRole) -> {
                  map.put(
                      accountRole.getAccountId(),
                      map.getOrDefault(accountRole.getAccountId(), 0) + 1);
                },
                HashMap::putAll);

    return accountPages.map(
        account -> {
          AccountListResponse response = new AccountListResponse();
          response.setId(account.getId());
          response.setUsername(account.getUserName());
          response.setRoleCount(accountRoleCountMap.getOrDefault(account.getId(), 0));
          response.setEnabled(account.getEnabled());
          response.setLocked(account.getLocked());
          return response;
        });
  }

  // Get account detail
  public AccountDetailResponse getAccountDetail(Integer accountId) {
    log.debug("[AccountService] getAccountDetail() accountId: {}", accountId);
    AccountDetailResponse response = new AccountDetailResponse();
    Account account = accountDataAccessService.getAccount(accountId);
    // Set account detail
    setAccountDetailResponse(response, account);
    // Set role detail
    List<Integer> roleIds = setRoleDetailResponse(response, account);
    // set scope detail
    setScopeDetailResponse(response, account, roleIds);
    return response;
  }

  /**
   * Update account roles and additional scopes
   *
   * @param accountId account id
   * @param request account update request
   */
  @Transactional
  public void updateAccount(Integer accountId, AccountUpdateRequest request) {
    Account account = accountDataAccessService.getAccount(accountId);
    boolean isChanged = false;
    // compare to old roles and additional scopes
    List<AccountRole> accountRoles = accountRoleDataAccessService.findAllByAccountId(accountId);
    HashSet<Integer> oldRoleIds =
        accountRoles.stream()
            .map(AccountRole::getRoleId)
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
    HashSet<Integer> newRoleIds =
        roleDataAccessService.findByCodeIn(request.getRoles()).stream()
            .map(Role::getId)
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
    // check if two list own the same elements
    if (!oldRoleIds.equals(newRoleIds)) {
      // delete old roles
      accountRoleDataAccessService.deleteByAccountId(accountId);
      // add new roles in one transaction
      accountRoleDataAccessService.saveAccountRoles(accountId, newRoleIds.stream().toList());
    }
    // check if additional scopes changed
    if (request.getAdditionalScopes() != null && !request.getAdditionalScopes().isEmpty()) {
      List<String> scopes = parseAdditionalScopes(account.getAdditionalScopes());
      HashSet<String> oldScopes =
          scopes.stream().collect(HashSet::new, HashSet::add, HashSet::addAll);
      HashSet<String> newScopes = new HashSet<>(request.getAdditionalScopes());
      if (!oldScopes.equals(newScopes)) {
        account.setAdditionalScopes(additionalScopesToDbFormat(request.getAdditionalScopes()));
        log.debug(
            "[AccountService] updateAccount() additionalScopes: {}", request.getAdditionalScopes());
        isChanged = true;
      }
      if (request.getEnabled() != null && !request.getEnabled().equals(account.getEnabled())) {
        account.setEnabled(request.getEnabled());
        isChanged = true;
      }
      if (request.getLocked() != null && !request.getLocked().equals(account.getLocked())) {
        account.setLocked(request.getLocked());
        isChanged = true;
      }
      if (isChanged) {
        account.setLastModifiedDate(LocalDateTime.now());
        accountDataAccessService.save(account);
      }
    }
  }

  /**
   * Create account
   *
   * @param request
   */
  @Transactional
  public Integer createAccount(AccountCreateRequest request) {
    Optional<Account> accountOptional =
        accountDataAccessService.getAccountByUsername(request.getUsername());
    if (accountOptional.isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }
    Account account = new Account();
    account.setUserName(request.getUsername());
    account.setPassword(SecretUtil.encodePassword(request.getPassword()));
    account.setLocked(false);
    account.setEnabled(true);
    account.setCredentialsExpired(false);
    account.setExpired(false);
    account.setCreatedDate(LocalDateTime.now());
    account.setCreatedBy(0);
    account.setLastModifiedDate(LocalDateTime.now());
    account.setLastModifiedBy(0);
    // add additional scopes
    if (request.getAdditionalScopes() != null && !request.getAdditionalScopes().isEmpty()) {
      // TODO check if scopes are valid
      String additionalScopes = additionalScopesToDbFormat(request.getAdditionalScopes());
      account.setAdditionalScopes(additionalScopes);
    } else {
      account.setAdditionalScopes("[]");
    }
    account = accountDataAccessService.save(account);
    // add roles
    List<Role> roles = roleDataAccessService.findByCodeIn(request.getRoles());
    accountRoleDataAccessService.saveAccountRoles(
        account.getId(), roles.stream().map(Role::getId).toList());
    return account.getId();
  }

  /**
   * 若設置軟刪除，則將 enabled 設置為 false
   *
   * @param accountId
   * @param request
   */
  @Transactional
  public void deleteAccount(Integer accountId, AccountDeleteRequest request) {
    log.debug("[AccountService] deleteAccount() accountId: {}, request: {}", accountId, request);
    if (request.isHardDelete()) {
      // delete account roles first
      accountRoleDataAccessService.deleteByAccountId(accountId);
      accountDataAccessService.deleteAccountByAccountId(accountId);
    } else {
      Account account = accountDataAccessService.getAccount(accountId);
      account.setEnabled(false);
      accountDataAccessService.save(account);
    }
  }

  /**
   * Simulate if account login with the client code Return what roles and scopes that this account
   * can access
   *
   * @param account
   * @param clientCode
   * @return
   */
  public SimulateLoginResponse simulateLogin(String account, String clientCode) {
    // Check if account exists
    Optional<Account> accountOptional = accountDataAccessService.getAccountByUsername(account);
    if (accountOptional.isEmpty()) {
      throw new IllegalArgumentException("Account not found");
    }
    Account accountEntity = accountOptional.get();
    // Check the client code
    Optional<OauthClient> clientOptional = clientDataAccessService.findByClientCode(clientCode);
    if (clientOptional.isEmpty()) {
      throw new IllegalArgumentException("Client not found");
    }
    OauthClient client = clientOptional.get();
    // New the response
    SimulateLoginResponse response = new SimulateLoginResponse();
    response.setAccountId(accountEntity.getId());
    response.setUserName(accountEntity.getUserName());
    response.setClientCode(client.getClientCode());
    // When login this client should get the roles and scopes
    // Roles should be accountRole
    // Scopes should be roleScope, clientScope and account additional scopes
    // Get account roles
    Integer accountId = accountEntity.getId();
    Integer clientId = client.getId();
    getAccountRoles(accountId, response);
    // Get client scopes (client + role + user)
    getAllScopes(clientId, accountEntity, response);
    // Get resource code
    getAccountResourceCode(clientId, response);
    return response;
  }

  /**
   * Check if account has permission to access the resource
   * And check if the account has the expected roles and scopes
   * @param request
   * @return
   */
  public CheckPermissionResponse checkPermission(CheckPermissionRequest request) {
    // Simulate login
    SimulateLoginResponse response = simulateLogin(request.getAccount(), request.getClientCode());
    CheckPermissionResponse checkRes = new CheckPermissionResponse();
    // Check if the resource is matched
    boolean resourcePermission = response.getResourceCodeList().contains(request.getResourceCode());
    // Check if the account has the expected roles and scopes
    boolean roleScopePermission = checkRolesAndScopes(request, response, checkRes);
    checkRes.setHasPermission(resourcePermission && roleScopePermission);
    return checkRes;
  }

  // Private methods

  // Set account detail
  private void setAccountDetailResponse(AccountDetailResponse response, Account account) {
    response.setId(account.getId());
    response.setUsername(account.getUserName());
    response.setLocked(account.getLocked());
    response.setEnabled(account.getEnabled());
    response.setCreatedTime(account.getCreatedDate());
    response.setUpdatedTime(account.getLastModifiedDate());
  }

  // Set role detail
  private List<Integer> setRoleDetailResponse(AccountDetailResponse response, Account account) {
    List<AccountRole> accountRoles =
        accountRoleDataAccessService.findAllByAccountId(account.getId());
    if (accountRoles.isEmpty()) {
      response.setRoles(List.of());
      return List.of();
    } else {
      // find all role information
      List<Role> roles =
          roleDataAccessService.findAllByIdIn(
              accountRoles.stream().map(AccountRole::getRoleId).toList());
      response.setRoles(roles.stream().map(Role::getCode).toList());
      return roles.stream().map(Role::getId).toList();
    }
  }

  private void setScopeDetailResponse(
      AccountDetailResponse response, Account account, List<Integer> roleIds) {
    // get all scope that this account can access
    List<String> additionalScopeList = new ArrayList<>();
    List<String> roleCopes = new ArrayList<>();
    List<String> accountScopes = new ArrayList<>();
    // additional scopes and role scopes
    String additionalScopes = account.getAdditionalScopes();
    // deal with additional scopes
    if (StringUtils.hasLength(additionalScopes) && !additionalScopes.equals("[]")) {
      List<String> scopes = parseAdditionalScopes(additionalScopes);
      log.debug("[AccountService] setScopeDetailResponse() additionalScopes: {}", scopes);
      additionalScopeList.addAll(scopes);
    }
    // deal with role scopes
    List<Integer> scopeIds =
        roleScopeDataAccessService.findAllByRoleIdIn(roleIds).stream()
            .map(RoleScope::getScopeId)
            .toList();
    List<Scope> scopes = scopeDataAccessService.findAllByIdIn(scopeIds);
    if (!scopes.isEmpty()) {
      roleCopes.addAll(scopes.stream().map(Scope::getScopeCode).toList());
    }
    response.setAdditionalScopes(additionalScopeList);
    response.setRoleScopes(roleCopes);
    // combine additional scopes and role scopes and distinct them
    accountScopes.addAll(additionalScopeList);
    accountScopes.addAll(roleCopes);
    response.setAvailableScopes(accountScopes.stream().distinct().toList());
  }

  /**
   * parse additional scopes ex: [ "backend.delete", "backend.write", "backend.modified",
   * "backend.audit", "backend.readonly" ] to backend.delete, backend.write, backend.modified,
   * backend.audit, backend.readonly
   *
   * @param additionalScopes
   * @return
   */
  private List<String> parseAdditionalScopes(String additionalScopes) {
    List<String> scopes =
        List.of(additionalScopes.substring(1, additionalScopes.length() - 1).split(","));
    // replace " and \ with empty string
    return scopes.stream().map(s -> s.replaceAll("\"", "").trim()).toList();
  }

  /**
   * additional scopes to db format ex: backend.delete, backend.write, backend.modified,
   * backend.audit, backend.readonly to
   * ["backend.delete","backend.write","backend.modified","backend.audit","backend.readonly"]
   *
   * @param additionalScopes
   * @return
   */
  private String additionalScopesToDbFormat(List<String> additionalScopes) {
    return "["
        + additionalScopes.stream()
            .map(s -> "\"" + s + "\"")
            .reduce((s1, s2) -> s1 + "," + s2)
            .orElse("")
        + "]";
  }

  /**
   * Get account roles by account
   *
   * @param accountId
   * @param response
   */
  private void getAccountRoles(Integer accountId, SimulateLoginResponse response) {
    List<AccountRole> accountRoles = accountRoleDataAccessService.findAllByAccountId(accountId);
    if (accountRoles.isEmpty()) {
      response.setRoleCodeList(List.of());
    }
    List<Role> roles =
        roleDataAccessService.findAllByIdIn(
            accountRoles.stream().map(AccountRole::getRoleId).toList());
    response.setRoleCodeList(roles.stream().map(Role::getCode).toList());
  }

  private void getAllScopes(Integer clientId, Account account, SimulateLoginResponse response) {
    // Client scopes
    dealWithClientScope(clientId, response);
    // Role scopes
    dealWithRoleScope(account.getId(), response);
    // Additional scopes
    dealWithAdditionalScope(account, response);
  }

  private void dealWithClientScope(Integer clientId, SimulateLoginResponse response) {
    List<OauthClientScope> clientScopes =
        clientScopeDataAccessService.findAllByOauthClientIdIn(List.of(clientId));
    if (clientScopes.isEmpty()) {
      response.setScopeCodeList(List.of());
    }
    List<Scope> scopes =
        scopeDataAccessService.findAllByIdIn(
            clientScopes.stream().map(OauthClientScope::getScopeId).toList());
    response.setScopeCodeList(scopes.stream().map(Scope::getScopeCode).toList());
  }

  private void dealWithRoleScope(Integer accountId, SimulateLoginResponse response) {
    List<AccountRole> accountRoles = accountRoleDataAccessService.findAllByAccountId(accountId);
    if (accountRoles.isEmpty()) {
      response.setScopeCodeList(List.of());
    }
    List<Integer> roleIds = accountRoles.stream().map(AccountRole::getRoleId).toList();
    List<RoleScope> roleScopes = roleScopeDataAccessService.findAllByRoleIdIn(roleIds);
    List<Scope> scopes =
        scopeDataAccessService.findAllByIdIn(
            roleScopes.stream().map(RoleScope::getScopeId).toList());
    response.setScopeCodeList(scopes.stream().map(Scope::getScopeCode).toList());
  }

  private void dealWithAdditionalScope(Account account, SimulateLoginResponse response) {
    String additionalScopes = account.getAdditionalScopes();
    if (StringUtils.hasLength(additionalScopes) && !additionalScopes.equals("[]")) {
      List<String> scopes = parseAdditionalScopes(additionalScopes);
      response.setScopeCodeList(scopes);
    }
  }

  private void getAccountResourceCode(Integer clientId, SimulateLoginResponse response) {
    List<OauthClientResource> clientResources =
        clientResourceDataAccessService.findAllByOauthClientIdIn(List.of(clientId));
    if (clientResources.isEmpty()) {
      response.setResourceCodeList(List.of());
    }
    // Get resource code
    List<Resource> resourceList =
        resourceDataAccessService.findAllByIdIn(
            clientResources.stream().map(OauthClientResource::getResourceId).toList());
    response.setResourceCodeList(resourceList.stream().map(Resource::getCode).toList());
  }

  /**
   * Check if the account has the expected roles and scopes
   * @param request
   * @param response
   * @param checkRes
   * @return
   */
  private boolean checkRolesAndScopes(CheckPermissionRequest request, SimulateLoginResponse response, CheckPermissionResponse checkRes) {
    // Check roles and list missing roles
    List<String> missingRoles = new ArrayList<>();
    List<String> matchedRoles = new ArrayList<>();
    for (String role : request.getExpectedRoles()) {
      if (!response.getRoleCodeList().contains(role)) {
        missingRoles.add(role);
      }
      else {
        matchedRoles.add(role);
      }
    }
    checkRes.setMissingRoles(new HashSet<>(missingRoles));
    checkRes.setMatchedRoles(new HashSet<>(matchedRoles));
    // Check scopes and list missing scopes
    List<String> missingScopes = new ArrayList<>();
    List<String> matchedScopes = new ArrayList<>();
    for (String scope : request.getExpectedScopes()) {
      if (!response.getScopeCodeList().contains(scope)) {
        missingScopes.add(scope);
      }
      else {
        matchedScopes.add(scope);
      }
    }
    checkRes.setMissingScopes(new HashSet<>(missingScopes));
    checkRes.setMatchedScopes(new HashSet<>(matchedScopes));
    return missingRoles.isEmpty() && missingScopes.isEmpty();
  }
}
