package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.OauthScopeDao;
import com.jasper.oauth.oauthdashboard.entity.OauthClientScope;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientScopeDataAccessService {
  private final OauthScopeDao oauthScopeDao;

  public ClientScopeDataAccessService(OauthScopeDao oauthScopeDao) {
    this.oauthScopeDao = oauthScopeDao;
  }

  public List<OauthClientScope> findAllByOauthClientIdIn(Collection<Integer> clientIdList) {
    return oauthScopeDao.findAllByOauthClientIdIn(clientIdList);
  }

  public Collection<OauthClientScope> saveAll(Collection<OauthClientScope> oauthClientScope) {
    return oauthScopeDao.saveAll(oauthClientScope);
  }

  public void deleteByOauthClientId(Integer clientId) {
    int count = oauthScopeDao.deleteByOauthClientId(clientId);
    log.debug("Deleted {} scopes for client id {}", count, clientId);
  }

  public List<OauthClientScope> findAllByScopeIdIn(Collection<Integer> scopeIdList) {
    return oauthScopeDao.findAllByScopeIdIn(scopeIdList);
  }
}
