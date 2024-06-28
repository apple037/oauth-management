package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.OauthClientGrantTypesDao;
import com.jasper.oauth.oauthdashboard.entity.OauthClientGrantTypes;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientGrantTypesDataAccessService {
  private final OauthClientGrantTypesDao oauthClientGrantTypesDao;

  public ClientGrantTypesDataAccessService(OauthClientGrantTypesDao oauthClientGrantTypesDao) {
    this.oauthClientGrantTypesDao = oauthClientGrantTypesDao;
  }

  public List<OauthClientGrantTypes> findAllByClientId(Integer clientId) {
    return oauthClientGrantTypesDao.findAllByClientId(clientId);
  }

  public Collection<OauthClientGrantTypes> saveAll(
      Collection<OauthClientGrantTypes> oauthClientGrantTypes) {
    return oauthClientGrantTypesDao.saveAll(oauthClientGrantTypes);
  }

  public void deleteByClientId(Integer clientId) {
    int count = oauthClientGrantTypesDao.deleteByClientId(clientId);
    log.debug("Deleted {} grant types for client id {}", count, clientId);
  }
}
