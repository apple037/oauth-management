package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.Scope;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeDao extends JpaRepository<Scope, Integer> {
  List<Scope> findAllByIdIn(Collection<Integer> scopeIdList);

  List<Scope> findByScopeCodeIn(Collection<String> scopeCodeList);

  Optional<Scope> findByScopeCode(String scopeCode);
}
