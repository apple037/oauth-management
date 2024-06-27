package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.ResourceDao;
import com.jasper.oauth.oauthdashboard.entity.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ResourceDataAccessService {
  private final ResourceDao resourceDao;

  public ResourceDataAccessService(ResourceDao resourceDao) {
    this.resourceDao = resourceDao;
  }

  /**
   * Find all resources by resource id list
   *
   * @param resourceIdList
   * @return
   */
  public List<Resource> findAllByIdIn(Collection<Integer> resourceIdList) {
    return resourceDao.findAllByIdIn(resourceIdList);
  }

  public Optional<Resource> findById(Integer resourceId) {
    return resourceDao.findById(resourceId);
  }

  public List<Resource> findAllByCodeIn(Collection<String> resourceCodeList) {
    return resourceDao.findAllByCodeIn(resourceCodeList);
  }

  // Get all resources from the database with pagination
  public Page<Resource> findAll(Pageable pageable) {
    return resourceDao.findAll(pageable);
  }

  public Resource save(Resource resource) {
    return resourceDao.save(resource);
  }

  public Optional<Resource> findByCode(String code) {
    return resourceDao.findByCode(code);
  }
}
