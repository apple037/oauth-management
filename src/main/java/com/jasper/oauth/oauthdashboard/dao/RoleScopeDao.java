package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.AccountRole;
import com.jasper.oauth.oauthdashboard.entity.Role;
import com.jasper.oauth.oauthdashboard.entity.RoleScope;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleScopeDao extends JpaRepository<RoleScope, Integer> {
  List<RoleScope> findAllByRoleIdIn(List<Integer> roleIdList);

  int deleteByRoleId(Integer roleId);

  List<RoleScope> findByScopeIdIn(Collection<Integer> scopeIdList);
}
