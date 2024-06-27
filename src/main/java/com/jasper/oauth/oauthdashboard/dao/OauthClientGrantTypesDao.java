package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.OauthClientGrantTypes;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientGrantTypesDao extends JpaRepository<OauthClientGrantTypes, Integer> {

  List<OauthClientGrantTypes> findAllByClientId(Integer clientId);

  int deleteByClientId(Integer clientId);
}
