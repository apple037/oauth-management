package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.RoleDao;
import com.jasper.oauth.oauthdashboard.entity.Role;
import com.jasper.oauth.oauthdashboard.service.AccountRoleService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleDataAccessService {
  private final RoleDao roleDao;
  private final AccountRoleService accountRoleService;

  public RoleDataAccessService(RoleDao roleDao, AccountRoleService accountRoleService) {
    this.roleDao = roleDao;
    this.accountRoleService = accountRoleService;
  }

  // Public methods

  public Optional<Role> findById(Integer roleId) {
    return roleDao.findById(roleId);
  }

  public Optional<Role> findByCode(String roleCode) {
    return roleDao.findByCode(roleCode);
  }

  // Find all roles by role id list
  public List<Role> findAllByIdIn(List<Integer> roleIdList) {
    return roleDao.findAllByIdIn(roleIdList);
  }

  public List<Role> findByCodeIn(List<String> roleCodeList) {
    return roleDao.findByCodeIn(roleCodeList);
  }

  public Page<Role> findAll(Pageable pageable) {
    return roleDao.findAll(pageable);
  }

  public Role save(Role role) {
    return roleDao.save(role);
  }

  public void deleteById(Integer roleId) {
    roleDao.deleteById(roleId);
  }
}
