package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.OauthClientResource;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthResourceDao extends JpaRepository<OauthClientResource, Integer> {
  List<OauthClientResource> findAllByClientIdIn(Collection<Integer> clientIdList);

  int deleteByClientId(Integer clientId);

  List<OauthClientResource> findAllByResourceIdIn(Collection<Integer> resourceIdList);
}
