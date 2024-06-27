package com.jasper.oauth.oauthdashboard.service;

import com.alibaba.fastjson2.JSON;
import com.jasper.oauth.oauthdashboard.constant.GrantTypeEnum;
import com.jasper.oauth.oauthdashboard.entity.OauthClient;
import com.jasper.oauth.oauthdashboard.entity.OauthClientGrantTypes;
import com.jasper.oauth.oauthdashboard.entity.OauthClientResource;
import com.jasper.oauth.oauthdashboard.entity.OauthClientScope;
import com.jasper.oauth.oauthdashboard.entity.Resource;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.client.ClientCreateAndUpdateRequest;
import com.jasper.oauth.oauthdashboard.model.client.ClientDetailResponse;
import com.jasper.oauth.oauthdashboard.model.client.ClientListResponse;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientGrantTypesDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ScopeDataAccessService;
import com.jasper.oauth.oauthdashboard.utils.DateUtils;
import com.jasper.oauth.oauthdashboard.utils.SecretUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Define the service logic for client */
@Slf4j
@Service
public class ClientService {
  private final ClientDataAccessService clientDataAccessService;
  private final ScopeDataAccessService scopeDataAccessService;
  private final ClientScopeDataAccessService clientScopeDataAccessService;
  private final ClientResourceDataAccessService clientResourceDataAccessService;
  private final ClientGrantTypesDataAccessService clientGrantTypesDataAccessService;
  private final ResourceDataAccessService resourceDataAccessService;

  public ClientService(
      ClientDataAccessService clientDataAccessService,
      ScopeDataAccessService scopeDataAccessService,
      ClientScopeDataAccessService clientScopeDataAccessService,
      ClientResourceDataAccessService clientResourceDataAccessService,
      ClientGrantTypesDataAccessService clientGrantTypesDataAccessService,
      ResourceDataAccessService resourceDataAccessService) {
    this.clientDataAccessService = clientDataAccessService;
    this.scopeDataAccessService = scopeDataAccessService;
    this.clientScopeDataAccessService = clientScopeDataAccessService;
    this.clientResourceDataAccessService = clientResourceDataAccessService;
    this.clientGrantTypesDataAccessService = clientGrantTypesDataAccessService;
    this.resourceDataAccessService = resourceDataAccessService;
  }

  public Page<ClientListResponse> getClientList(PageParam pageParam) {
    Pageable pageable = pageParam.toPageable();
    Page<OauthClient> clientPage = clientDataAccessService.findAll(pageable);
    if (clientPage.isEmpty()) {
      return Page.empty();
    }
    // deal with the scope count and resource count
    Set<Integer> clientIdSet =
        clientPage.stream().map(OauthClient::getId).collect(Collectors.toSet());
    HashMap<Integer, Long> scopeCountMap = getClientScopeCountMap(clientIdSet);
    //    log.debug("scopeCountMap: {}", scopeCountMap);
    // deal with resource count
    HashMap<Integer, Long> resourceCountMap = getClientResourceCountMap(clientIdSet);
    //    log.debug("resourceCountMap: {}", resourceCountMap);
    // build the response
    return clientPage.map(
        client -> {
          ClientListResponse clientListResponse = new ClientListResponse();
          clientListResponse.setId(client.getId());
          clientListResponse.setCode(client.getClientCode());
          int scopeCount =
              scopeCountMap.get(client.getId()) == null
                  ? 0
                  : scopeCountMap.get(client.getId()).intValue();
          int resourceCount =
              resourceCountMap.get(client.getId()) == null
                  ? 0
                  : resourceCountMap.get(client.getId()).intValue();
          clientListResponse.setScopeCount(scopeCount);
          clientListResponse.setResourceCount(resourceCount);
          return clientListResponse;
        });
  }

  /**
   * Get the client detail by client id
   * @param clientId
   * @return
   */
  public ClientDetailResponse getClientDetail(Integer clientId) {
    Optional<OauthClient> clientOptional = clientDataAccessService.findById(clientId);
    if (clientOptional.isEmpty()) {
      return null;
    }
    OauthClient client = clientOptional.get();
    ClientDetailResponse clientDetailResponse = new ClientDetailResponse();
    clientDetailResponse.setId(client.getId());
    clientDetailResponse.setCode(client.getClientCode());
    clientDetailResponse.setServiceCode(client.getServiceCode());
    clientDetailResponse.setSystemCode(client.getSystemCode());
    clientDetailResponse.setAccessTokenValidity(Long.valueOf(client.getAccessTokenValidity()));
    clientDetailResponse.setRefreshTokenValidity(Long.valueOf(client.getRefreshTokenValidity()));
    clientDetailResponse.setAdditionalInformation(client.getAdditionalInformation());
    clientDetailResponse.setCreatedBy(client.getCreatedBy());
    clientDetailResponse.setCreatedDate(DateUtils.getDateString(client.getCreatedDate()));
    clientDetailResponse.setLastModifiedBy(client.getLastModifiedBy());
    clientDetailResponse.setLastModifiedDate(DateUtils.getDateString(client.getLastModifiedDate()));
    // Get the grant type column
    getClientGrantTypeDetail(clientDetailResponse, clientId);
    // Get the resource column
    getClientResourceDetail(clientDetailResponse, clientId);
    // Get the scope column
    getClientScopeDetail(clientDetailResponse, clientId);
    return clientDetailResponse;
  }

  @Transactional
  public Integer createClient(ClientCreateAndUpdateRequest request) {
    // check client code duplication
    Optional<OauthClient> clientOptional =
        clientDataAccessService.findByClientCode(request.getClientCode());
    if (clientOptional.isPresent()) {
      throw new IllegalArgumentException("Client code already exists");
      }
    // build the client entity
    OauthClient client = new OauthClient();
    buildEntityByRequest(client, request);
    // save the client entity
    client = clientDataAccessService.save(client);
    Integer clientId = client.getId();
    // deal with grant type
    createClientGrantType(request.getGrantTypeList(), clientId);
    // deal with client resource
    createClientResource(request.getResourceCodeList(), clientId);
    // deal with client scope
    createClientScope(request.getScopeCodeList(), clientId);
    return clientId;
  }

  @Transactional
  public void updateClient(Integer clientId, ClientCreateAndUpdateRequest request) {
    // check client exists
    Optional<OauthClient> clientOptional = clientDataAccessService.findById(clientId);
    if (clientOptional.isEmpty()) {
      throw new IllegalArgumentException("Client not exists");
    }
    OauthClient client = clientOptional.get();
    // check client code duplication
    if (!client.getClientCode().equals(request.getClientCode())) {
      Optional<OauthClient> clientOptionalNew =
          clientDataAccessService.findByClientCode(request.getClientCode());
      if (clientOptionalNew.isPresent()) {
        throw new IllegalArgumentException("Client code already exists");
      }
    }
    // build the client entity
    buildEntityByRequest(client, request);
    // save the client entity
    clientDataAccessService.save(client);
    // deal with grant type
    clientGrantTypesDataAccessService.deleteByClientId(clientId);
    createClientGrantType(request.getGrantTypeList(), clientId);
    // deal with client resource
    clientResourceDataAccessService.deleteByClientId(clientId);
    createClientResource(request.getResourceCodeList(), clientId);
    // deal with client scope
    clientScopeDataAccessService.deleteByOauthClientId(clientId);
    createClientScope(request.getScopeCodeList(), clientId);
  }

  @Transactional
  public void deleteClient(Integer clientId) {
    // check client exists
    Optional<OauthClient> clientOptional = clientDataAccessService.findById(clientId);
    if (clientOptional.isEmpty()) {
      throw new IllegalArgumentException("Client not exists");
    }
    // delete the grant type
    clientGrantTypesDataAccessService.deleteByClientId(clientId);
    // delete the client resource
    clientResourceDataAccessService.deleteByClientId(clientId);
    // delete the client scope
    clientScopeDataAccessService.deleteByOauthClientId(clientId);
    // delete the client entity
    clientDataAccessService.deleteById(clientId);
  }

  // Private methods

  /**
   * Get the scope count for each client
   *
   * @param clientIdSet
   * @return
   */
  private HashMap<Integer, Long> getClientScopeCountMap(Set<Integer> clientIdSet) {
    // Get all scopes for the clients
    List<OauthClientScope> clientScopeList =
        clientScopeDataAccessService.findAllByOauthClientIdIn(clientIdSet);
    // Count the scope for each client
    return clientScopeList.stream()
        .collect(Collectors.groupingBy(OauthClientScope::getOauthClientId, Collectors.counting()))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, HashMap::new));
  }

  /**
   * Get the resource count for each client
   *
   * @param clientIdSet
   * @return
   */
  private HashMap<Integer, Long> getClientResourceCountMap(Set<Integer> clientIdSet) {
    // Get all resources for the clients
    List<OauthClientResource> clientResourceList =
        clientResourceDataAccessService.findAllByOauthClientIdIn(clientIdSet);
    // Count the resource for each client
    return clientResourceList.stream()
        .collect(Collectors.groupingBy(OauthClientResource::getClientId, Collectors.counting()))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, HashMap::new));
  }

  /**
   * Get the grant type by client id
   *
   * @param clientDetailResponse
   * @param clientId
   */
  private void getClientGrantTypeDetail(
      ClientDetailResponse clientDetailResponse, Integer clientId) {
    // Get the grant type by client id
    List<OauthClientGrantTypes> grantTypeList =
        clientGrantTypesDataAccessService.findAllByClientId(clientId);
    if (grantTypeList.isEmpty()) {
      clientDetailResponse.setGrantTypeList(new ArrayList<>());
    }
    clientDetailResponse.setGrantTypeList(
        grantTypeList.stream()
            .map(OauthClientGrantTypes::getGrantType)
            .collect(Collectors.toList()));
  }

  /**
   * Get the resource by client id
   *
   * @param clientDetailResponse
   * @param clientId
   */
  private void getClientResourceDetail(
      ClientDetailResponse clientDetailResponse, Integer clientId) {
    // Get the resource by client id
    List<OauthClientResource> resourceList =
        clientResourceDataAccessService.findAllByOauthClientIdIn(List.of(clientId));
    if (resourceList.isEmpty()) {
      clientDetailResponse.setResourceCodeList(new ArrayList<>());
    }
    // Get the resource code list
    Set<Integer> resourceIdSet =
        resourceList.stream().map(OauthClientResource::getResourceId).collect(Collectors.toSet());
    List<String> resourceCodeList =
        resourceDataAccessService.findAllByIdIn(resourceIdSet).stream()
            .map(Resource::getCode)
            .toList();
    clientDetailResponse.setResourceCodeList(resourceCodeList);
  }

  private void getClientScopeDetail(ClientDetailResponse clientDetailResponse, Integer clientId) {
    // Get the scope by client id
    List<OauthClientScope> scopeList =
        clientScopeDataAccessService.findAllByOauthClientIdIn(List.of(clientId));
    if (scopeList.isEmpty()) {
      clientDetailResponse.setScopeCodeList(new ArrayList<>());
    }
    // Get the scope code list
    Set<Integer> scopeIdSet =
        scopeList.stream().map(OauthClientScope::getScopeId).collect(Collectors.toSet());
    List<String> scopeCodeList =
        scopeDataAccessService.findAllByIdIn(scopeIdSet).stream()
            .map(com.jasper.oauth.oauthdashboard.entity.Scope::getScopeCode)
            .toList();
    clientDetailResponse.setScopeCodeList(scopeCodeList);
  }

  /**
   * Build the client entity by request
   * @param client
   * @param request
   * @return
   */
  private OauthClient buildEntityByRequest(OauthClient client, ClientCreateAndUpdateRequest request) {
    // build the client entity
    client.setClientCode(request.getClientCode());
    client.setClientSecret(SecretUtil.encodePassword(request.getClientSecret()));
    client.setServiceCode(request.getServiceCode());
    client.setWebServerRedirectUrl(request.getWebServerRedirectUri() == null ? "" : request.getWebServerRedirectUri());
    client.setSystemCode(request.getSystemCode());
    client.setAccessTokenValidity(request.getAccessTokenValidity().intValue());
    client.setRefreshTokenValidity(request.getRefreshTokenValidity().intValue());
    client.setAdditionalInformation(request.getAdditionalInformation());
    client.setLastModifiedBy(0);
    client.setLastModifiedDate(DateUtils.getCurrentDateTime());
    checkAdditionalInformation(request.getAdditionalInformation());
    client.setAdditionalInformation(request.getAdditionalInformation());
    return client;
  }

  /**
   * Check the additional information format
   * @param additionalInformation
   */
  private void checkAdditionalInformation(String additionalInformation) {
    // check the additional information format should be like json format
    try {
      Object obj = JSON.parse(additionalInformation);
      log.debug("obj: {}", obj);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("The additional information format is not correct");
    }
  }

  /**
   * Create the client grant type
   * @param grantTypeList
   * @param clientId
   */
  private void createClientGrantType(List<String> grantTypeList, Integer clientId) {
    if (grantTypeList == null || grantTypeList.isEmpty()) {
      return;
    }
    Set<OauthClientGrantTypes> grantTypeSet = new HashSet<>();
    grantTypeList.forEach(
        grantType -> {
          // Check format of grant type
          if (!GrantTypeEnum.contains(grantType)) {
            throw new IllegalArgumentException("Grant type not exists");
          }
          OauthClientGrantTypes clientGrantTypes = new OauthClientGrantTypes();
          clientGrantTypes.setClientId(clientId);
          clientGrantTypes.setGrantType(grantType);
          clientGrantTypes.setCreatedBy(0);
          clientGrantTypes.setCreatedDate(DateUtils.getCurrentDateTime());
          clientGrantTypes.setLastModifiedBy(0);
          clientGrantTypes.setLastModifiedDate(DateUtils.getCurrentDateTime());
          grantTypeSet.add(clientGrantTypes);
        });
    if (!grantTypeSet.isEmpty()) {
      Collection<OauthClientGrantTypes> savedSet = clientGrantTypesDataAccessService.saveAll(grantTypeSet);
      if (savedSet.size() != grantTypeSet.size()) {
        throw new IllegalArgumentException("Create client grant type failed");
      }
    }
  }

  /**
   * Create the client resource
   * @param resourceCodeList
   * @param clientId
   */
  private void createClientResource(List<String> resourceCodeList, Integer clientId) {
    if (resourceCodeList == null || resourceCodeList.isEmpty()) {
      return;
    }
    List<Resource> resourceList = resourceDataAccessService.findAllByCodeIn(resourceCodeList);
    Map<String, Resource> resourceCodeMap =
        resourceList.stream().collect(Collectors.toMap(Resource::getCode, resource -> resource));
    Set<OauthClientResource> resourceSet = new HashSet<>();
    resourceCodeList.forEach(
        resourceCode -> {
          Optional<Resource> resourceOptional = Optional.ofNullable(resourceCodeMap.get(resourceCode));
          if (resourceOptional.isEmpty()) {
            throw new IllegalArgumentException("Resource code not exists");
          }
          OauthClientResource clientResource = new OauthClientResource();
          clientResource.setClientId(clientId);
          clientResource.setResourceId(resourceOptional.get().getId());
          clientResource.setCreatedBy(0);
          clientResource.setCreatedDate(DateUtils.getCurrentDateTime());
          clientResource.setLastModifiedBy(0);
          clientResource.setLastModifiedDate(DateUtils.getCurrentDateTime());
          resourceSet.add(clientResource);
        });
    if (!resourceSet.isEmpty()) {
      Collection<OauthClientResource> savedSet = clientResourceDataAccessService.saveAll(resourceSet);
      if (savedSet.size() != resourceSet.size()) {
        throw new IllegalArgumentException("Create client resource failed");
      }
    }
  }

  private void createClientScope(List<String> scopeCodeList, Integer clientId) {
    if (scopeCodeList == null || scopeCodeList.isEmpty()) {
      return;
    }
    List<Scope> scopeList = scopeDataAccessService.findByScopeCodeIn(scopeCodeList);
    Map<String, Scope> scopeCodeMap =
        scopeList.stream().collect(Collectors.toMap(Scope::getScopeCode, scope -> scope));
    Set<OauthClientScope> scopeSet = new HashSet<>();
    scopeCodeList.forEach(
        scopeCode -> {
          Optional<Scope> scopeOptional = Optional.ofNullable(scopeCodeMap.get(scopeCode));
          if (scopeOptional.isEmpty()) {
            throw new IllegalArgumentException("Scope code not exists");
          }
          OauthClientScope clientScope = new OauthClientScope();
          clientScope.setOauthClientId(clientId);
          clientScope.setScopeId(scopeOptional.get().getId());
          clientScope.setCreatedBy(0);
          clientScope.setCreatedDate(DateUtils.getCurrentDateTime());
          clientScope.setLastModifiedBy(0);
          clientScope.setLastModifiedDate(DateUtils.getCurrentDateTime());
          scopeSet.add(clientScope);
        });
    if (!scopeSet.isEmpty()) {
      Collection<OauthClientScope> savedSet = clientScopeDataAccessService.saveAll(scopeSet);
      if (savedSet.size() != scopeSet.size()) {
        throw new IllegalArgumentException("Create client scope failed");
      }
    }
  }
}
