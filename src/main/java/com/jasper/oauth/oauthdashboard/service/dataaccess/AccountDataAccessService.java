package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.AccountDao;
import com.jasper.oauth.oauthdashboard.dao.RoleScopeDao;
import com.jasper.oauth.oauthdashboard.entity.Account;
import com.jasper.oauth.oauthdashboard.entity.AccountRole;
import com.jasper.oauth.oauthdashboard.entity.Role;
import com.jasper.oauth.oauthdashboard.entity.RoleScope;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.account.AccountCreateRequest;
import com.jasper.oauth.oauthdashboard.model.account.AccountDeleteRequest;
import com.jasper.oauth.oauthdashboard.model.account.AccountDetailResponse;
import com.jasper.oauth.oauthdashboard.model.account.AccountListResponse;
import com.jasper.oauth.oauthdashboard.model.account.AccountUpdateRequest;
import com.jasper.oauth.oauthdashboard.service.AccountRoleService;
import com.jasper.oauth.oauthdashboard.service.RoleService;
import com.jasper.oauth.oauthdashboard.service.ScopeService;
import com.jasper.oauth.oauthdashboard.utils.SecretUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class AccountDataAccessService {
  private final AccountDao accountDao;

  public AccountDataAccessService(
      AccountDao accountDao) {
    this.accountDao = accountDao;
  }

  /**
   * Get all accounts
   * @param pageable
   * @return
   */
  public Page<Account> findAll(Pageable pageable) {
    return accountDao.findAll(pageable);
  }

  /**
   * Get account by account id
   *
   * @param accountId
   * @return
   */
  public Account getAccount(Integer accountId) {
   return accountDao
        .findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
  }

  /**
   * Get account by username
   * @param username
   * @return
   */
  public Optional<Account> getAccountByUsername(String username) {
    return accountDao
        .findByUserName(username);
  }

  /**
   * Save account
   * @param account
   * @return
   */
  public Account save(Account account) {
    return accountDao.save(account);
  }

  /**
   * Delete account by account id
   * @param accountId
   */
  public void deleteAccountByAccountId(Integer accountId) {
    accountDao.deleteById(accountId);
  }

  public List<Account> findAllByAdditionalInformationLike(String scopeCode) {
    String scopeLikeString = "%" + scopeCode + "%";
    return accountDao.findAllByAdditionalScopesLike(scopeLikeString);
  }
}
