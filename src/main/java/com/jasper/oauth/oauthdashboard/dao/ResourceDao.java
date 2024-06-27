package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceDao extends JpaRepository<Resource, Integer> {
  List<Resource> findAllByIdIn(Collection<Integer> resourceIdList);

  List<Resource> findAllByCodeIn(Collection<String> resourceCodeList);

  Optional<Resource> findByCode(String code);
}
