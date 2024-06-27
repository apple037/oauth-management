package com.jasper.oauth.oauthdashboard.service;

import com.jasper.oauth.oauthdashboard.entity.OauthClient;
import com.jasper.oauth.oauthdashboard.entity.OauthClientResource;
import com.jasper.oauth.oauthdashboard.entity.Resource;
import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.resource.ResourceCreateRequest;
import com.jasper.oauth.oauthdashboard.model.resource.ResourceDetailResponse;
import com.jasper.oauth.oauthdashboard.model.resource.ResourceListResponse;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ClientResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.service.dataaccess.ResourceDataAccessService;
import com.jasper.oauth.oauthdashboard.utils.DateUtils;
import java.util.ArrayList;
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

@Service
@Slf4j
public class ResourceService {
  private final ResourceDataAccessService resourceDataAccessService;
  private final ClientResourceDataAccessService clientResourceDataAccessService;
  private final ClientDataAccessService clientDataAccessService;

  public ResourceService(
      ResourceDataAccessService resourceDataAccessService,
      ClientResourceDataAccessService clientResourceDataAccessService,
      ClientDataAccessService clientDataAccessService) {
    this.resourceDataAccessService = resourceDataAccessService;
    this.clientResourceDataAccessService = clientResourceDataAccessService;
    this.clientDataAccessService = clientDataAccessService;
  }

  // Public methods

  /**
   * Get all resource list with pagination
   *
   * @param pageParam
   * @return
   */
  public Page<ResourceListResponse> getResourceList(PageParam pageParam) {
    Pageable pageable = pageParam.toPageable();
    Page<Resource> resourcePage = resourceDataAccessService.findAll(pageable);
    if (resourcePage.isEmpty()) {
      return Page.empty();
    }
    // Get all client resource list
    Set<Integer> resourceIdList =
        resourcePage.getContent().stream().map(Resource::getId).collect(Collectors.toSet());
    List<OauthClientResource> clientResourceList =
        clientResourceDataAccessService.findAllByResourceIdIn(resourceIdList);
    HashMap<Integer, Integer> clientResourceCountMap =
        clientResourceList.stream()
            .map(OauthClientResource::getResourceId)
            .collect(
                Collectors.toMap(
                    resourceId -> resourceId, resourceId -> 1, Integer::sum, HashMap::new));
    return resourcePage.map(
        resource -> {
          Integer clientResourceCount = clientResourceCountMap.getOrDefault(resource.getId(), 0);
          return ResourceListResponse.builder()
              .id(resource.getId())
              .code(resource.getCode())
              .label(resource.getLabel())
              .clientCount(clientResourceCount)
              .build();
        });
  }

  /**
   * Get resource detail by resource id
   *
   * @param resourceId
   * @return
   */
  public ResourceDetailResponse getResourceDetail(Integer resourceId) {
    // check if resource exists
    Optional<Resource> resourceOptional = resourceDataAccessService.findById(resourceId);
    if (resourceOptional.isEmpty()) {
      throw new IllegalArgumentException("Resource not found");
    }
    Resource resource = resourceOptional.get();
    // Find all client resource list
    List<String> clientCodeList = new ArrayList<>();
    List<OauthClientResource> clientResourceList =
        clientResourceDataAccessService.findAllByResourceIdIn(List.of(resourceId));
    if (!clientResourceList.isEmpty()) {
      Set<Integer> clientIdList =
          clientResourceList.stream()
              .map(OauthClientResource::getClientId)
              .collect(Collectors.toSet());
      clientCodeList =
          clientDataAccessService.findAllByClientIdIn(clientIdList).stream()
              .map(OauthClient::getClientCode)
              .collect(Collectors.toList());
    }
    // Build response
    return ResourceDetailResponse.builder()
        .id(resource.getId())
        .code(resource.getCode())
        .label(resource.getLabel())
        .description(resource.getDescription())
        .clientCodeList(clientCodeList)
        .createdBy(resource.getCreatedBy())
        .createdDate(DateUtils.formatDateTime(resource.getCreatedDate()))
        .updatedBy(resource.getLastModifiedBy())
        .updatedDate(DateUtils.formatDateTime(resource.getLastModifiedDate()))
        .build();
  }

  @Transactional
  public Integer createResource(ResourceCreateRequest request) {
    // check if resource code exists
    Optional<Resource> resourceOptional = resourceDataAccessService.findByCode(request.getCode());
    if (resourceOptional.isPresent()) {
      throw new IllegalArgumentException("Resource code already exists");
    }
    Resource resource = new Resource();
    resource.setCode(request.getCode());
    resource.setLabel(request.getLabel());
    resource.setDescription(request.getDescription());
    resource.setCreatedBy(0);
    resource.setCreatedDate(DateUtils.getCurrentDateTime());
    resource.setLastModifiedBy(0);
    resource.setLastModifiedDate(DateUtils.getCurrentDateTime());
    resource = resourceDataAccessService.save(resource);
    return resource.getId();
  }
}
