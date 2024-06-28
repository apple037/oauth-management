package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.OauthResourceDao;
import com.jasper.oauth.oauthdashboard.entity.OauthClientResource;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientResourceDataAccessService {
  private final OauthResourceDao oauthResourceDao;

  public ClientResourceDataAccessService(OauthResourceDao oauthResourceDao) {
    this.oauthResourceDao = oauthResourceDao;
  }

  public List<OauthClientResource> findAllByOauthClientIdIn(Collection<Integer> clientIdList) {
    return oauthResourceDao.findAllByClientIdIn(clientIdList);
  }

  public Collection<OauthClientResource> saveAll(
      Collection<OauthClientResource> oauthClientResource) {
    return oauthResourceDao.saveAll(oauthClientResource);
  }

  public void deleteByClientId(Integer clientId) {
    int count = oauthResourceDao.deleteByClientId(clientId);
    log.debug("Deleted {} resources for client id {}", count, clientId);
  }

  public List<OauthClientResource> findAllByResourceIdIn(Collection<Integer> resourceIdList) {
    return oauthResourceDao.findAllByResourceIdIn(resourceIdList);
  }
}
