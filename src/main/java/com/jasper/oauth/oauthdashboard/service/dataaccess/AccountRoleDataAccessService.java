package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.AccountRoleDao;
import com.jasper.oauth.oauthdashboard.entity.AccountRole;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountRoleDataAccessService {
  private final AccountRoleDao accountRoleDao;

  public AccountRoleDataAccessService(AccountRoleDao accountRoleDao) {
    this.accountRoleDao = accountRoleDao;
  }

  // Public methods
  // Find all account roles by account id list
  public List<AccountRole> findAllByAccountIdIn(List<Integer> accountIdList) {
    return accountRoleDao.findAllByAccountIdIn(accountIdList);
  }

  // Find all account roles by a single account id
  public List<AccountRole> findAllByAccountId(Integer accountId) {
    return accountRoleDao.findAllByAccountId(accountId);
  }

  // delete account role by account id
  @Transactional
  public void deleteByAccountId(Integer accountId) {
    accountRoleDao.deleteByAccountId(accountId);
  }

  // Save account roles by account id
  @Transactional
  public void saveAccountRoles(Integer accountId, List<Integer> roleIdList) {
    // Save account roles
    List<AccountRole> accountRoleList = new ArrayList<>();
    for (Integer roleId : roleIdList) {
      AccountRole accountRole = new AccountRole();
      accountRole.setAccountId(accountId);
      accountRole.setRoleId(roleId);
      accountRole.setCreatedBy(0);
      accountRole.setCreatedDate(LocalDateTime.now());
      accountRole.setLastModifiedBy(0);
      accountRole.setLastModifiedDate(LocalDateTime.now());
      accountRoleList.add(accountRole);
    }
    if (accountRoleList.isEmpty()) {
      return;
    }
    accountRoleDao.saveAll(accountRoleList);
  }

  /**
   * Find all account roles by role id list
   * @param roleIdList
   * @return
   */
  public List<AccountRole> findAllByRoleIdIn(List<Integer> roleIdList) {
    return accountRoleDao.findAllByRoleIdIn(roleIdList);
  }

  public void deleteByRoleId(Integer roleId) {
    int count = accountRoleDao.deleteByRoleId(roleId);
    log.debug("Deleted {} account roles for role id {}", count, roleId);
  }
}
