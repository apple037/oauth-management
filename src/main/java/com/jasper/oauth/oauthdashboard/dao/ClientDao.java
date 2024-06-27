package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.OauthClient;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDao extends JpaRepository<OauthClient, Integer> {
  Optional<OauthClient> findByClientCode(String clientCode);

  List<OauthClient> findAllByIdIn(Collection<Integer> clientIdList);
}
