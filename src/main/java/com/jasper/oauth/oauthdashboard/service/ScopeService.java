package com.jasper.oauth.oauthdashboard.service;

import com.jasper.oauth.oauthdashboard.entity.OauthClient;
import com.jasper.oauth.oauthdashboard.entity.OauthClientScope;
import com.jasper.oauth.oauthdashboard.entity.Resource;
import com.jasper.oauth.oauthdashboard.entity.Role;
import com.jasper.oauth.oauthdashboard.entity.RoleScope;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.scope.ScopeCreateRequest;
import com.jasper.oauth.oauthdashboard.model.scope.ScopeDetailResponse;
import com.jasper.oauth.oauthdashboard.model.scope.ScopeListResponse;
import com.jasper.oauth.oauthdashboard.service.dataaccess.AccountDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.RoleDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.RoleScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.utils.DateUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ScopeService {
  private final ScopeDataAccessService scopeDataAccessService;
  private final RoleScopeDataAccessService roleScopeDataAccessService;
  private final ClientScopeDataAccessService clientScopeDataAccessService;
  private final AccountDataAccessService accountDataAccessService;
  private final ResourceDataAccessService resourceDataAccessService;
  private final RoleDataAccessService roleDataAccessService;
  private final ClientDataAccessService clientDataAccessService;

  public ScopeService(
      ScopeDataAccessService scopeDataAccessService,
      RoleScopeDataAccessService roleScopeDataAccessService,
      ClientScopeDataAccessService clientScopeDataAccessService,
      AccountDataAccessService accountDataAccessService,
      ResourceDataAccessService resourceDataAccessService,
      RoleDataAccessService roleDataAccessService,
      ClientDataAccessService clientDataAccessService) {
    this.scopeDataAccessService = scopeDataAccessService;
    this.roleScopeDataAccessService = roleScopeDataAccessService;
    this.clientScopeDataAccessService = clientScopeDataAccessService;
    this.accountDataAccessService = accountDataAccessService;
    this.resourceDataAccessService = resourceDataAccessService;
    this.roleDataAccessService = roleDataAccessService;
    this.clientDataAccessService = clientDataAccessService;
  }

  // Public methods

  /**
   * Get scope list in page
   *
   * @param pageParam
   * @return
   */
  public Page<ScopeListResponse> getScopeList(PageParam pageParam) {
    Pageable pageable = pageParam.toPageable();
    Page<Scope> scopePage = scopeDataAccessService.findAll(pageable);
    if (scopePage.isEmpty()) {
      return Page.empty();
    }
    Set<Integer> scopeIdSet = scopePage.map(Scope::getId).toSet();
    Set<String> scopeCodeSet = scopePage.map(Scope::getScopeCode).toSet();
    Set<Integer> resourceIdSet = scopePage.map(Scope::getResourceId).toSet();
    // find resource id to code map
    HashMap<Integer, String> resourceIdCodeMap = getResourceCodeMap(resourceIdSet);
    // find all roleScope and clientScope count
    HashMap<Integer, Integer> roleScopeCountMap = getRoleScopeCountMap(scopeIdSet);
    HashMap<Integer, Integer> clientScopeCountMap = getClientScopeCountMap(scopeIdSet);
    // TODO
    HashMap<Integer, Integer> userScopeCountMap = getUserScopeCountMap(scopeCodeSet);
    // build response
    return scopePage.map(
        scope -> {
          ScopeListResponse response = new ScopeListResponse();
          response.setId(scope.getId());
          response.setScopeCode(scope.getScopeCode());
          response.setLabel(scope.getLabel());
          response.setResourceName(resourceIdCodeMap.getOrDefault(scope.getResourceId(), ""));
          response.setRoleScopeCount(roleScopeCountMap.getOrDefault(scope.getId(), 0));
          response.setClientScopeCount(clientScopeCountMap.getOrDefault(scope.getId(), 0));
          response.setUserScopeCount(userScopeCountMap.getOrDefault(scope.getId(), 0));
          return response;
        });
  }

  /**
   * Get scope detail
   *
   * @param scopeId
   * @return
   */
  public ScopeDetailResponse getScopeDetail(Integer scopeId) {
    // Check scope exists
    Optional<Scope> scopeOptional = scopeDataAccessService.findById(scopeId);
    if (scopeOptional.isEmpty()) {
      throw new IllegalArgumentException("Scope not found");
    }
    Scope scope = scopeOptional.get();
    // find resource id to code map
    HashMap<Integer, String> resourceIdCodeMap = getResourceCodeMap(Set.of(scope.getResourceId()));
    String resourceCode = resourceIdCodeMap.get(scope.getResourceId());
    if (!StringUtils.hasLength(resourceCode)) {
      throw new IllegalArgumentException("Resource not found");
    }
    ScopeDetailResponse response = new ScopeDetailResponse();
    response.setId(scope.getId());
    response.setResourceCode(resourceCode);
    response.setScopeCode(scope.getScopeCode());
    response.setLabel(scope.getLabel());
    response.setCreatedBy(scope.getCreatedBy());
    response.setCreatedDate(DateUtils.formatDateTime(scope.getCreatedDate()));
    response.setUpdatedBy(scope.getLastModifiedBy());
    response.setUpdatedDate(DateUtils.formatDateTime(scope.getLastModifiedDate()));
    // role code list
    getRoleScopeList(scopeId, response);
    // client code list
    getClientScopeList(scopeId, response);
    // TODO account list
    getAccountList(scope.getScopeCode(), response);
    return response;
  }

  @Transactional
  public Integer createScope(ScopeCreateRequest request) {
    // check scope code duplicate
    Optional<Scope> scopeOptional = scopeDataAccessService.findByScopeCode(request.getScopeCode());
    if (scopeOptional.isPresent()) {
      throw new IllegalArgumentException("Scope code already exists");
    }
    // check resource exists
    Optional<Resource> resourceOptional =
        resourceDataAccessService.findByCode(request.getResourceCode());
    if (resourceOptional.isEmpty()) {
      throw new IllegalArgumentException("Resource not found");
    }
    Resource resource = resourceOptional.get();
    // create scope
    Scope scope = new Scope();
    scope.setResourceId(resource.getId());
    scope.setScopeCode(request.getScopeCode());
    scope.setLabel(request.getLabel());
    scope.setCreatedBy(0);
    scope.setCreatedDate(DateUtils.getCurrentDateTime());
    scope.setLastModifiedBy(0);
    scope.setLastModifiedDate(DateUtils.getCurrentDateTime());
    scope = scopeDataAccessService.save(scope);
    return scope.getId();
  }

  // Private methods

  private HashMap<Integer, String> getResourceCodeMap(Set<Integer> resourceIdSet) {
    // find all resource
    List<Resource> resourceList = resourceDataAccessService.findAllByIdIn(resourceIdSet);
    if (resourceList.isEmpty()) {
      return new HashMap<>();
    }
    // build resource id to code map
    return (HashMap<Integer, String>)
        resourceList.stream().collect(Collectors.toMap(Resource::getId, Resource::getCode));
  }

  /**
   * Get role scope count map
   *
   * @param scopeIdSet
   * @return
   */
  private HashMap<Integer, Integer> getRoleScopeCountMap(Set<Integer> scopeIdSet) {
    // find all roleScope
    List<RoleScope> roleScopeList = roleScopeDataAccessService.findByScopeIdIn(scopeIdSet);
    if (roleScopeList.isEmpty()) {
      return new HashMap<>();
    }
    // build roleScope count map, scopeId -> count
    return (HashMap<Integer, Integer>)
        roleScopeList.stream()
            .collect(Collectors.groupingBy(RoleScope::getScopeId, Collectors.summingInt(e -> 1)));
  }

  /**
   * Get client scope count map
   *
   * @param scopeIdSet
   * @return
   */
  private HashMap<Integer, Integer> getClientScopeCountMap(Set<Integer> scopeIdSet) {
    // find all clientScope
    List<OauthClientScope> clientScopeList =
        clientScopeDataAccessService.findAllByScopeIdIn(scopeIdSet);
    if (clientScopeList.isEmpty()) {
      return new HashMap<>();
    }
    // build clientScope count map, scopeId -> count
    return (HashMap<Integer, Integer>)
        clientScopeList.stream()
            .collect(
                Collectors.groupingBy(OauthClientScope::getScopeId, Collectors.summingInt(e -> 1)));
  }

  /**
   * Get user scope count map
   *
   * @param scopeCodeSet
   * @return
   */
  // TODO: 先不做 太麻煩
  private HashMap<Integer, Integer> getUserScopeCountMap(Set<String> scopeCodeSet) {
    return new HashMap<>();
  }

  /**
   * Get role code list
   *
   * @param scopeId
   * @param response
   */
  private void getRoleScopeList(Integer scopeId, ScopeDetailResponse response) {
    // find all roleScope
    List<RoleScope> roleScopeList = roleScopeDataAccessService.findByScopeIdIn(List.of(scopeId));
    if (roleScopeList.isEmpty()) {
      response.setRoleCodeList(List.of());
    }
    // Get role code list
    List<Role> roleList =
        roleDataAccessService.findAllByIdIn(
            roleScopeList.stream().map(RoleScope::getRoleId).collect(Collectors.toList()));
    // build role code list
    response.setRoleCodeList(roleList.stream().map(Role::getCode).collect(Collectors.toList()));
  }

  /**
   * Get client code list
   *
   * @param scopeId
   * @param response
   */
  private void getClientScopeList(Integer scopeId, ScopeDetailResponse response) {
    // find all clientScope
    List<OauthClientScope> clientScopeList =
        clientScopeDataAccessService.findAllByScopeIdIn(List.of(scopeId));
    if (clientScopeList.isEmpty()) {
      response.setClientCodeList(List.of());
    }
    // get client code list
    List<OauthClient> clientList =
        clientDataAccessService.findAllByClientIdIn(
            clientScopeList.stream()
                .map(OauthClientScope::getOauthClientId)
                .collect(Collectors.toList()));
    // build client code list
    response.setClientCodeList(
        clientList.stream().map(OauthClient::getClientCode).collect(Collectors.toList()));
  }

  /**
   * Get account list
   *
   * @param scopeCode
   * @param response
   */
  private void getAccountList(String scopeCode, ScopeDetailResponse response) {
    // TODO 先不做 太麻煩
    response.setAccountList(List.of());
  }
}
