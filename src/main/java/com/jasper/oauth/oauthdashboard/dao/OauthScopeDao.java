package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.OauthClientScope;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthScopeDao extends JpaRepository<OauthClientScope, Integer> {
  List<OauthClientScope> findAllByOauthClientIdIn(Collection<Integer> clientIdList);

  int deleteByOauthClientId(Integer clientId);

  List<OauthClientScope> findAllByScopeIdIn(Collection<Integer> scopeIdList);
}
