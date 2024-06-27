package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.ScopeDao;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScopeDataAccessService {
  private final ScopeDao scopeDao;

  public ScopeDataAccessService(ScopeDao scopeDao) {
    this.scopeDao = scopeDao;
  }

  // Public methods

  // Find all scopes by scope id list
  public List<Scope> findAllByIdIn(Collection<Integer> scopeIdList) {
    return scopeDao.findAllByIdIn(scopeIdList);
  }

  // Find all scopes by scope code list
  public List<Scope> findByScopeCodeIn(Collection<String> scopeCodeList) {
    return scopeDao.findByScopeCodeIn(scopeCodeList);
  }

  public Page<Scope> findAll(Pageable pageable) {
    return scopeDao.findAll(pageable);
  }

  public Optional<Scope> findById(Integer scopeId) {
    return scopeDao.findById(scopeId);
  }

  public Optional<Scope> findByScopeCode(String scopeCode) {
    return scopeDao.findByScopeCode(scopeCode);
  }

  public Scope save(Scope scope) {
    return scopeDao.save(scope);
  }
}
