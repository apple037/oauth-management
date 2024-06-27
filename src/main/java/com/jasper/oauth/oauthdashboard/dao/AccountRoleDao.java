package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.AccountRole;
import com.jasper.oauth.oauthdashboard.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleDao extends JpaRepository<AccountRole, Integer> {
  // Find all roles in account id list
  List<AccountRole> findAllByAccountIdIn(List<Integer> accountIdList);

  // Find all roles in account id
  List<AccountRole> findAllByAccountId(Integer accountId);

  void deleteByAccountId(Integer accountId);

  List<AccountRole> findAllByRoleIdIn(List<Integer> roleIdList);

  int deleteByRoleId(Integer roleId);
}
