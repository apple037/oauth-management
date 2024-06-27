package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {
  List<Role> findAllByIdIn(List<Integer> roleIdList);

  List<Role> findByCodeIn(List<String> roleCodeList);

  Optional<Role> findByCode(String roleCode);
}
