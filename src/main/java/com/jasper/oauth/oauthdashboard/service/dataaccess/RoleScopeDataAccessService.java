package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.RoleScopeDao;
import com.jasper.oauth.oauthdashboard.entity.RoleScope;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleScopeDataAccessService {
  private final RoleScopeDao roleScopeDao;

  public RoleScopeDataAccessService(RoleScopeDao roleScopeDao) {
    this.roleScopeDao = roleScopeDao;
  }

  // Public methods

  // Find all scopes by scope id list
  public List<RoleScope> findAllByRoleIdIn(List<Integer> roleIdList) {
    return roleScopeDao.findAllByRoleIdIn(roleIdList);
  }

  public List<RoleScope> saveAll(List<RoleScope> roleScopes) {
    return roleScopeDao.saveAll(roleScopes);
  }

  // Delete role scopes by role id
  public void deleteByRoleId(Integer roleId) {
    int count = roleScopeDao.deleteByRoleId(roleId);
    log.debug("Deleted {} role scopes for role id {}", count, roleId);
  }

  public List<RoleScope> findByScopeIdIn(Collection<Integer> scopeIdList) {
    return roleScopeDao.findByScopeIdIn(scopeIdList);
  }
}
