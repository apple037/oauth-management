package com.jasper.oauth.oauthdashboard.service;

import com.jasper.oauth.oauthdashboard.entity.AccountRole;
import com.jasper.oauth.oauthdashboard.entity.Resource;
import com.jasper.oauth.oauthdashboard.entity.Role;
import com.jasper.oauth.oauthdashboard.entity.RoleScope;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.role.RoleCreateRequest;
import com.jasper.oauth.oauthdashboard.model.role.RoleDetailResponse;
import com.jasper.oauth.oauthdashboard.model.role.RoleListResponse;
import com.jasper.oauth.oauthdashboard.model.role.RoleUpdateRequest;
import com.jasper.oauth.oauthdashboard.model.scope.ScopeDto;
import com.jasper.oauth.oauthdashboard.service.dataaccess.AccountRoleDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.RoleDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.RoleScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ScopeDataAccessService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RoleService {
  private final AccountRoleDataAccessService accountRoleDataAccessService;
  private final RoleDataAccessService roleDataAccessService;
  private final RoleScopeDataAccessService roleScopeDataAccessService;
  private final ScopeDataAccessService scopeDataAccessService;
  private final ResourceDataAccessService resourceDataAccessService;

  public RoleService(
      AccountRoleDataAccessService accountRoleDataAccessService,
      RoleDataAccessService roleDataAccessService,
      RoleScopeDataAccessService roleScopeDataAccessService,
      ScopeDataAccessService scopeDataAccessService,
      ResourceDataAccessService resourceDataAccessService) {
    this.accountRoleDataAccessService = accountRoleDataAccessService;
    this.roleDataAccessService = roleDataAccessService;
    this.roleScopeDataAccessService = roleScopeDataAccessService;
    this.scopeDataAccessService = scopeDataAccessService;
    this.resourceDataAccessService = resourceDataAccessService;
  }

  // Public methods

  /**
   * Get role list
   *
   * @param pageParam
   * @return
   */
  public Page<RoleListResponse> getRoleList(PageParam pageParam) {
    Pageable pageable = pageParam.toPageable();
    Page<Role> rolePage = roleDataAccessService.findAll(pageable);
    if (rolePage.isEmpty()) {
      return Page.empty();
    }
    List<Integer> roleIdList = rolePage.getContent().stream().map(Role::getId).toList();
    // Get account count by role id in role list
    List<AccountRole> accountRoleList = accountRoleDataAccessService.findAllByRoleIdIn(roleIdList);
    HashMap<Integer, Integer> roleIdAccountCountMap =
        accountRoleList.stream()
            .collect(
                HashMap::new,
                (map, accountRole) ->
                    map.put(
                        accountRole.getRoleId(), map.getOrDefault(accountRole.getRoleId(), 0) + 1),
                HashMap::putAll);
    // Convert role page to role list response
    return rolePage.map(
        role -> {
          RoleListResponse roleListResponse = new RoleListResponse();
          roleListResponse.setId(role.getId());
          roleListResponse.setCode(role.getCode());
          roleListResponse.setLabel(role.getLabel());
          roleListResponse.setAccountCount(roleIdAccountCountMap.getOrDefault(role.getId(), 0));
          return roleListResponse;
        });
  }

  /**
   * Get role detail
   *
   * @param roleId
   * @return
   */
  public RoleDetailResponse getRoleDetail(Integer roleId) {
    Optional<Role> roleOptional = roleDataAccessService.findById(roleId);
    if (roleOptional.isEmpty()) {
      throw new IllegalArgumentException("Role not found");
    }
    Role role = roleOptional.get();
    RoleDetailResponse roleDetailResponse = new RoleDetailResponse();
    roleDetailResponse.setId(role.getId());
    roleDetailResponse.setCode(role.getCode());
    roleDetailResponse.setLabel(role.getLabel());
    roleDetailResponse.setCreatedBy(role.getCreatedBy());
    roleDetailResponse.setCreatedDate(role.getCreatedDate());
    roleDetailResponse.setUpdatedBy(role.getLastModifiedBy());
    roleDetailResponse.setUpdatedDate(role.getLastModifiedDate());
    // ScopeDto column
    getScopeDtoColumn(roleDetailResponse);
    // Account count column
    getAccountCountColumn(roleDetailResponse);
    return roleDetailResponse;
  }

  @Transactional
  public void createRole(RoleCreateRequest request) {
    // 檢查 code 是否重複
    Optional<Role> roleOptional = roleDataAccessService.findByCode(request.getCode());
    if (roleOptional.isPresent()) {
      throw new IllegalArgumentException("Role code already exists");
    }
    checkRoleFormat(request.getCode());
    // 檢查 scope 是否存在
    if (request.getScopeCodes() == null || request.getScopeCodes().isEmpty()) {
      throw new IllegalArgumentException("Scope code is required");
    }
    List<Scope> scopeList = checkScopeExists(request.getScopeCodes());
    // 建立 Role
    Role role = new Role();
    role.setCode(request.getCode());
    role.setLabel(request.getLabel());
    role.setCreatedBy(0);
    role.setCreatedDate(LocalDateTime.now());
    role.setLastModifiedBy(0);
    role.setLastModifiedDate(LocalDateTime.now());
    role = roleDataAccessService.save(role);
    // 建立 RoleScope
    if (request.getScopeCodes() == null || request.getScopeCodes().isEmpty()) {
      return;
    }
    int roleId = role.getId();
    batchSaveRoleScope(scopeList, request.getScopeCodes(), roleId);
  }

  @Transactional
  public void updateRole(Integer roleId, RoleUpdateRequest request) {
    // Check role exists
    Optional<Role> roleOptional = roleDataAccessService.findById(roleId);
    if (roleOptional.isEmpty()) {
      throw new IllegalArgumentException("Role not found");
    }
    Role role = roleOptional.get();
    // Check role code duplicate
    checkRoleCodeAvailable(role, request.getCode());
    // Check scope exists
    List<Scope> scopeList = checkScopeExists(request.getScopeCodes());
    // Update role
    role.setCode(request.getCode());
    role.setLabel(request.getLabel());
    role.setLastModifiedBy(0);
    role.setLastModifiedDate(LocalDateTime.now());
    roleDataAccessService.save(role);
    // update role scope
    // delete all role scope by role id
    roleScopeDataAccessService.deleteByRoleId(roleId);
    // insert new role scope
    batchSaveRoleScope(scopeList, request.getScopeCodes(), roleId);
  }

  /**
   * Delete role Be aware to delete role, it will delete all role scope and account role
   *
   * @param roleId
   */
  @Transactional
  public void deleteRole(Integer roleId) {
    // check role exists
    Optional<Role> roleOptional = roleDataAccessService.findById(roleId);
    if (roleOptional.isEmpty()) {
      throw new IllegalArgumentException("Role not found");
    }
    Role role = roleOptional.get();
    // delete role scope
    roleScopeDataAccessService.deleteByRoleId(role.getId());
    // delete account role
    accountRoleDataAccessService.deleteByRoleId(role.getId());
    // delete role
    roleDataAccessService.deleteById(role.getId());
  }

  // Private methods

  private void checkRoleFormat(String roleCode) {
    if (roleCode == null || roleCode.isEmpty()) {
      throw new IllegalArgumentException("Role code is required");
    }
    if (roleCode.length() > 50) {
      throw new IllegalArgumentException("Role code is too long");
    }
    // code 必須是以 "ROLE_" 開頭
    if (!roleCode.startsWith("ROLE_")) {
      throw new IllegalArgumentException("Role code must start with 'ROLE_'");
    }
  }

  /**
   * Get scope info for role detail response
   *
   * @param roleDetailResponse
   */
  private void getScopeDtoColumn(RoleDetailResponse roleDetailResponse) {
    // 取得 RoleScope 資料
    Integer roleId = roleDetailResponse.getId();
    List<RoleScope> roleScopeList = roleScopeDataAccessService.findAllByRoleIdIn(List.of(roleId));
    if (roleScopeList.isEmpty()) {
      roleDetailResponse.setScopes(List.of());
    }
    // 取得 Scope 資料
    List<Integer> scopeIdList = roleScopeList.stream().map(RoleScope::getScopeId).toList();
    List<Scope> scopeList = scopeDataAccessService.findAllByIdIn(scopeIdList);
    // 取得 scope 對應 resource 資料
    Set<Integer> resourceIdSet =
        scopeList.stream().map(Scope::getResourceId).collect(Collectors.toSet());
    List<Resource> resourceList = resourceDataAccessService.findAllByIdIn(resourceIdSet);
    // HashMap<resourceId, Resource>
    Map<Integer, Resource> resourceMap =
        resourceList.stream().collect(Collectors.toMap(Resource::getId, resource -> resource));
    // build scopeDtoList
    List<ScopeDto> scopeDtoList =
        scopeList.stream()
            .map(
                scope -> {
                  Resource resource = resourceMap.get(scope.getResourceId());
                  return ScopeDto.builder()
                      .id(scope.getId())
                      .scopeCode(scope.getScopeCode())
                      .scopeLabel(scope.getLabel())
                      .resourceCode(resource.getCode())
                      .resourceDesc(resource.getLabel())
                      .build();
                })
            .toList();
    roleDetailResponse.setScopes(scopeDtoList);
  }

  /**
   * Get account count for role detail response
   *
   * @param roleDetailResponse
   */
  private void getAccountCountColumn(RoleDetailResponse roleDetailResponse) {
    Integer roleId = roleDetailResponse.getId();
    List<AccountRole> accountRoleList =
        accountRoleDataAccessService.findAllByRoleIdIn(List.of(roleId));
    roleDetailResponse.setAccountCount(accountRoleList.size());
  }

  /**
   * Check scope exists
   *
   * @param scopeCodes
   */
  private List<Scope> checkScopeExists(Collection<String> scopeCodes) {
    List<Scope> scopeList = scopeDataAccessService.findByScopeCodeIn(scopeCodes);
    if (scopeList.size() != scopeCodes.size()) {
      throw new IllegalArgumentException("Scope not found");
    }
    return scopeList;
  }

  private void batchSaveRoleScope(List<Scope> scopeList, Set<String> scopeCodeSet, Integer roleId) {
    List<RoleScope> roleScopeList =
        scopeList.stream()
            .map(
                scope -> {
                  RoleScope roleScope = new RoleScope();
                  roleScope.setRoleId(roleId);
                  roleScope.setScopeId(scope.getId());
                  roleScope.setCreatedBy(0);
                  roleScope.setCreatedDate(LocalDateTime.now());
                  roleScope.setLastModifiedBy(0);
                  roleScope.setLastModifiedDate(LocalDateTime.now());
                  return roleScope;
                })
            .toList();
    roleScopeList = roleScopeDataAccessService.saveAll(roleScopeList);
    if (roleScopeList.size() != scopeCodeSet.size()) {
      throw new IllegalArgumentException("Create role scope failed");
    }
  }

  /**
   * Check role code available
   *
   * @param role
   * @param roleCode
   */
  private void checkRoleCodeAvailable(Role role, String roleCode) {
    if (role.getCode().equals(roleCode)) {
      return;
    }
    Optional<Role> roleOptional = roleDataAccessService.findByCode(roleCode);
    if (roleOptional.isPresent()) {
      throw new IllegalArgumentException("Role code already exists");
    }
  }
}
